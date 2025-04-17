import java.util.*;
import java.util.Objects;

class CandidateVotes implements Comparable<CandidateVotes> {
    String name;
    int votes;

    CandidateVotes(String name, int votes) {
        this.name = name;
        this.votes = votes;
    }

    void addVote() {
        this.votes++;
    }

    void setVotes(int votes) {
        this.votes = votes;
    }

    String getName() {
        return name;
    }

    int getVotes() {
        return votes;
    }

    @Override
    public int compareTo(CandidateVotes other) {
        return Integer.compare(other.votes, this.votes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateVotes that = (CandidateVotes) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name + " - " + votes;
    }
}

class Election {
    private PriorityQueue<CandidateVotes> maxHeap;
    private HashMap<String, CandidateVotes> candidateMap;
    private ArrayList<String> candidateNames;
    private int totalVotesCast = 0;
    private int maxElectorateVotes = 0;

    public Election() {
        maxHeap = new PriorityQueue<>();
        candidateMap = new HashMap<>();
        candidateNames = new ArrayList<>();
    }

    public void initializeCandidates(LinkedList<String> candidates) {
        maxHeap.clear();
        candidateMap.clear();
        candidateNames.clear();
        this.totalVotesCast = 0;

        for (String name : candidates) {
            if (!candidateMap.containsKey(name)) {
                CandidateVotes cv = new CandidateVotes(name, 0);
                candidateMap.put(name, cv);
                maxHeap.add(cv);
                candidateNames.add(name);
            }
        }
    }

    public void setElectorateSize(int p) {
        this.maxElectorateVotes = p;
    }

    public void castVote(String candidate) {
        CandidateVotes cv = candidateMap.get(candidate);
        if (cv != null) {
            maxHeap.remove(cv);
            cv.addVote();
            maxHeap.add(cv);
            totalVotesCast++;
        } else {
            System.out.println("Warning: Candidate '" + candidate + "' not found for voting.");
        }
    }

    public void castRandomVote() {
        if (candidateNames.isEmpty()) {
            System.out.println("Warning: No candidates initialized to cast a random vote.");
            return;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(candidateNames.size());
        String randomCandidate = candidateNames.get(randomIndex);
        castVote(randomCandidate);
    }

    public void rigElection(String candidate) {
        CandidateVotes targetCandidate = candidateMap.get(candidate);
        if (targetCandidate == null) {
            System.out.println("Warning: Candidate '" + candidate + "' cannot be rigged - not found.");
            return;
        }

        int currentMaxVotes = 0;
        PriorityQueue<CandidateVotes> tempHeap = new PriorityQueue<>(maxHeap);
        while (!tempHeap.isEmpty()) {
            CandidateVotes top = tempHeap.poll();
            if (!top.equals(targetCandidate)) {
                currentMaxVotes = top.getVotes();
                break;
            }
        }

        tempHeap = null;


        int votesNeededToWin = (currentMaxVotes + 1);

        if (targetCandidate.getVotes() < votesNeededToWin) {
            maxHeap.remove(targetCandidate);
            targetCandidate.setVotes(votesNeededToWin);
            maxHeap.add(targetCandidate);
            System.out.println("Election rigged for " + candidate);
        } else {
            System.out.println(candidate + " is already strictly winning. No rigging needed.");
        }
    }


    public List<String> getTopKCandidates(int k) {
        List<String> topK = new ArrayList<>();
        PriorityQueue<CandidateVotes> tempHeap = new PriorityQueue<>(maxHeap);

        int count = 0;
        while (count < k && !tempHeap.isEmpty()) {
            CandidateVotes cv = tempHeap.poll();
            topK.add(cv.getName());
            count++;
        }
        return topK;
    }

    public void auditElection() {
        List<CandidateVotes> sortedList = new ArrayList<>();
        PriorityQueue<CandidateVotes> tempHeap = new PriorityQueue<>(maxHeap);

        while (!tempHeap.isEmpty()) {
            sortedList.add(tempHeap.poll());
        }

        System.out.println("Election Audit:");
        for (CandidateVotes cv : sortedList) {
            System.out.println(cv.toString());
        }
    }
}

public class ElectionSystem {
    public static void main(String[] args) {
        Election election = new Election();

        LinkedList<String> candidates = new LinkedList<>(Arrays.asList(
                "Marcus Fenix", "Dominic Santiago", "Damon Baird", "Cole Train", "Anya Stroud"));
        election.initializeCandidates(candidates);
        System.out.println("Candidates initialized: " + candidates);

        int p = 5;
        election.setElectorateSize(p);
        System.out.println("Electorate size set to: " + p);


        System.out.println("\nCasting votes...");
        election.castVote("Cole Train");
        System.out.println("Cast vote for Cole Train");
        election.castVote("Cole Train");
        System.out.println("Cast vote for Cole Train");
        election.castVote("Marcus Fenix");
        System.out.println("Cast vote for Marcus Fenix");
        election.castVote("Anya Stroud");
        System.out.println("Cast vote for Anya Stroud");
        election.castVote("Anya Stroud");
        System.out.println("Cast vote for Anya Stroud");

        System.out.println("\nGetting top 3 candidates after 5 votes...");
        List<String> top3 = election.getTopKCandidates(3);
        System.out.println("Top 3 candidates: " + top3);

        System.out.println("\nRigging election for Marcus Fenix...");
        election.rigElection("Marcus Fenix");

        System.out.println("\nGetting top 3 candidates after rigging...");
        List<String> top3Rigged = election.getTopKCandidates(3);
        System.out.println("Top 3 candidates after rigging: " + top3Rigged);

        System.out.println("\nRunning Election Audit...");
        election.auditElection();
    }
}