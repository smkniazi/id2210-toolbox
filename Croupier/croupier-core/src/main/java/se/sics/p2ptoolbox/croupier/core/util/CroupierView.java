package se.sics.p2ptoolbox.croupier.core.util;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import se.sics.gvod.net.VodAddress;
import se.sics.p2ptoolbox.croupier.api.CroupierSelectionPolicy;
import se.sics.p2ptoolbox.croupier.api.util.CroupierPeerView;

public class CroupierView {

    private final int viewSize;
    private final VodAddress selfAddress;
    private List<CroupierViewEntry> entries;
    private HashMap<VodAddress, CroupierViewEntry> d2e;
    private final Random rand;
    
    private Comparator<CroupierViewEntry> comparatorByAge = new Comparator<CroupierViewEntry>() {
        @Override
        public int compare(CroupierViewEntry o1, CroupierViewEntry o2) {
            if (o1.getDescriptor().getAge() > o2.getDescriptor().getAge()) {
                return 1;
            } else if (o1.getDescriptor().getAge() < o2.getDescriptor().getAge()) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    //-------------------------------------------------------------------	
    public CroupierView(VodAddress selfAddress, int viewSize, Random rand) {
        super();
        this.selfAddress = selfAddress;
        this.viewSize = viewSize;
        this.entries = new ArrayList<CroupierViewEntry>();
        this.d2e = new HashMap<VodAddress, CroupierViewEntry>();
        this.rand = rand;
    }

//-------------------------------------------------------------------	
    public void incrementDescriptorAges() {
        for (CroupierViewEntry entry : entries) {
            entry.getDescriptor().incrementAge();
        }
    }

    public VodAddress selectPeerToShuffleWith(CroupierSelectionPolicy policy,
            boolean softmax, double temperature) {
        if (entries.isEmpty()) {
            return null;
        }

        CroupierViewEntry selectedEntry = null;

        if (!softmax || policy == CroupierSelectionPolicy.RANDOM) {
            if (policy == CroupierSelectionPolicy.TAIL) {
                selectedEntry = Collections.max(entries, comparatorByAge);
            } else if (policy == CroupierSelectionPolicy.HEALER) {
                selectedEntry = Collections.max(entries, comparatorByAge);
            } else if (policy == CroupierSelectionPolicy.RANDOM) {
                selectedEntry = entries.get(rand.nextInt(entries.size()));
            } else {
                throw new IllegalArgumentException("Invalid Croupier policy selected:" +
                        policy);
            } 
        
        } else {

            List<CroupierViewEntry> tempEntries =
                    new ArrayList<CroupierViewEntry>(entries);
            if (policy == CroupierSelectionPolicy.TAIL) {
                Collections.sort(tempEntries, comparatorByAge);
            } else if (policy == CroupierSelectionPolicy.HEALER) {
                Collections.sort(tempEntries, comparatorByAge);
            } else {
                throw new IllegalArgumentException("Invalid Croupier policy selected:" +
                        policy);
            }            

            double rnd = rand.nextDouble();
            double total = 0.0d;
            double[] values = new double[tempEntries.size()];
            int j = tempEntries.size() + 1;
            for (int i = 0; i < tempEntries.size(); i++) {
                // get inverse of values - lowest have highest value.
                double val = j;
                j--;
                values[i] = Math.exp(val / temperature);
                total += values[i];
            }

            boolean found = false;
            for (int i = 0; i < values.length; i++) {
                if (i != 0) {
                    values[i] += values[i - 1];
                }
                // normalise the probability
                double normalisedReward = values[i] / total;
                if (normalisedReward >= rnd) {
                    selectedEntry = tempEntries.get(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                selectedEntry = tempEntries.get(tempEntries.size() - 1);
            }
        }

        // TODO - by not removing a reference to the node I am shuffling with, we
        // break the 'batched random walk' (Cyclon) behaviour. But it's more important
        // to keep the graph connected.
        if (entries.size() >= viewSize) {
            removeEntry(selectedEntry);
        }

        return selectedEntry.getDescriptor().src;
    }

//-------------------------------------------------------------------	
    public List<CroupierPeerView> selectToSendAtInitiator(int count, VodAddress destinationPeer) {
        List<CroupierViewEntry> randomEntries = generateRandomSample(count);
        List<CroupierPeerView> descriptors = new ArrayList<CroupierPeerView>();
        for (CroupierViewEntry cacheEntry : randomEntries) {
            cacheEntry.sentTo(destinationPeer);
            descriptors.add(cacheEntry.getDescriptor());
        }
        return descriptors;
    }

//-------------------------------------------------------------------	
    public List<CroupierPeerView> selectToSendAtReceiver(int count, VodAddress destinationPeer) {
        List<CroupierViewEntry> randomEntries = generateRandomSample(count);
        List<CroupierPeerView> descriptors = new ArrayList<CroupierPeerView>();
        for (CroupierViewEntry cacheEntry : randomEntries) {
            cacheEntry.sentTo(destinationPeer);
            descriptors.add(cacheEntry.getDescriptor());
        }
        return descriptors;
    }

//-------------------------------------------------------------------	
    public void selectToKeep(VodAddress from, ImmutableCollection<CroupierPeerView> descriptors) {
        if (from.equals(selfAddress)) {
            return;
        }
        LinkedList<CroupierViewEntry> entriesSentToThisPeer = new LinkedList<CroupierViewEntry>();
        CroupierViewEntry fromEntry = d2e.get(from);
        if (fromEntry != null) {
            entriesSentToThisPeer.add(fromEntry);
        }

        for (CroupierViewEntry cacheEntry : entries) {
            if (cacheEntry.wasSentTo(from)) {
                entriesSentToThisPeer.add(cacheEntry);
            }
        }

        for (CroupierPeerView descriptor : descriptors) {
            if (selfAddress.equals(descriptor.src)) {
                // do not keep descriptor of self
                continue;
            }
            if (d2e.containsKey(descriptor.src)) {
                // we already have an entry for this peer. keep the youngest one
                CroupierViewEntry entry = d2e.get(descriptor.src);
                if (entry.getDescriptor().getAge() > descriptor.getAge()) {
                    // we keep the lowest age descriptor
                    CroupierViewEntry newCVE = new CroupierViewEntry(descriptor);
                    
                    int index = entriesSentToThisPeer.indexOf(entry);
                    if(index != -1) {
                        entriesSentToThisPeer.set(index, newCVE);
                    }
                    
                    removeEntry(entry);
                    addEntry(newCVE);
                }
                continue;
            }
            if (entries.size() < viewSize) {
                // fill an empty slot
                addEntry(new CroupierViewEntry(descriptor));
                continue;
            }
            // replace one slot out of those sent to this peer
            CroupierViewEntry sentEntry = entriesSentToThisPeer.poll();
            if (sentEntry != null) {
                removeEntry(sentEntry);
                addEntry(new CroupierViewEntry(descriptor));
            }
        }
    }

//-------------------------------------------------------------------	
    public final List<CroupierPeerView> getAll() {
        List<CroupierPeerView> descriptors = new ArrayList<CroupierPeerView>();
        for (CroupierViewEntry cacheEntry : entries) {
            descriptors.add(cacheEntry.getDescriptor());
        }
        return descriptors;
    }

//-------------------------------------------------------------------	
    public final List<VodAddress> getAllAddress() {
        List<VodAddress> all = new ArrayList<VodAddress>();
        for (CroupierViewEntry cacheEntry : entries) {
            all.add(cacheEntry.getDescriptor().src);
        }
        return all;
    }

//-------------------------------------------------------------------	
    public final List<VodAddress> getRandomPeers(int count) {
        List<CroupierViewEntry> randomEntries = generateRandomSample(count);
        List<VodAddress> randomPeers = new ArrayList<VodAddress>();

        for (CroupierViewEntry cacheEntry : randomEntries) {
            randomPeers.add(cacheEntry.getDescriptor().src);
        }

        return randomPeers;
    }

//-------------------------------------------------------------------	
    private List<CroupierViewEntry> generateRandomSample(int n) {
        List<CroupierViewEntry> randomEntries;
        if (n >= entries.size()) {
            // return all entries
            randomEntries = new ArrayList<CroupierViewEntry>(entries);
        } else {
            // return count random entries
            randomEntries = new ArrayList<CroupierViewEntry>();
            // Don Knuth, The Art of Computer Programming, Algorithm S(3.4.2)
            int t = 0, m = 0, N = entries.size();
            while (m < n) {
                int x = rand.nextInt(N - t);
                if (x < n - m) {
                    randomEntries.add(entries.get(t));
                    m += 1;
                    t += 1;
                } else {
                    t += 1;
                }
            }
        }
        return randomEntries;
    }

//-------------------------------------------------------------------	
    private void addEntry(CroupierViewEntry entry) {

        //TODO Alex reenable the port fix
        // if the entry refers to a stun port, change it to the default port.
//        if (entry.getDescriptor().src.getPort() == VodConfig.DEFAULT_STUN_PORT || entry.getDescriptor().src.getPort() == VodConfig.DEFAULT_STUN_PORT_2) {
//            entry.getDescriptor().src.getPeerAddress().setPort(VodConfig.DEFAULT_PORT);
//        }

        // don't add yourself
        if (entry.getDescriptor().src.equals(selfAddress)) {
            return;
        }
        
        if (!entries.contains(entry)) {
            entries.add(entry);
            d2e.put(entry.getDescriptor().src, entry);
            checkSize();
        } else {
            // replace the entry if it already exists
            removeEntry(entry);
            addEntry(entry);
        }
    }

//-------------------------------------------------------------------	
    private void removeEntry(CroupierViewEntry entry) {
        entries.remove(entry);
        CroupierViewEntry removed = d2e.remove(entry.getDescriptor().src);
        if(entries.size() != d2e.size()) {
            System.err.println("Croupier View corrupted after removing:" + entry + " removed:" + removed);
        } 
        checkSize();
    }

    public void timedOut(VodAddress node) {
        CroupierViewEntry entry = d2e.get(node);
        if (entry == null) {
            return;
        }
        removeEntry(entry);
    }

//-------------------------------------------------------------------	
    private void checkSize() {
        if (entries.size() != d2e.size()) {
            StringBuilder sb = new StringBuilder("Entries: \n");
            for (CroupierViewEntry d : entries) {
                sb.append(d.toString()).append(", ");
            }
            sb.append(" \n IndexEntries: \n");
            for (CroupierViewEntry d : d2e.values()) {
                sb.append(d.toString()).append(", ");
            }
            System.err.println(sb.toString());
            throw new RuntimeException("WHD " + entries.size() + " <> " + d2e.size());
        }
    }

//-------------------------------------------------------------------
    public void initialize(Set<CroupierPeerView> insiders) {
        for (CroupierPeerView cpv : insiders) {
            if (!cpv.src.equals(selfAddress)) {
                addEntry(new CroupierViewEntry(cpv));
            }
        }
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public int size() {
        return this.entries.size();
    }
}
