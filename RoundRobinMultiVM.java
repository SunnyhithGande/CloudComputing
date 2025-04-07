import java.util.*;

class Cloudlet {
    int id, length, remainingLength;
    double arrivalTime, startTime, finishTime;

    public Cloudlet(int id, int length, double arrivalTime) {
        this.id = id;
        this.length = length;
        this.remainingLength = length;
        this.arrivalTime = arrivalTime;
    }
}

class VM extends Thread {
    int id, mips, ram, storage;
    List<Cloudlet> cloudletList;
    int timeQuantum;
    double totalExecutionTime = 0;

    public VM(int id, int mips, int ram, int storage, int timeQuantum) {
        this.id = id;
        this.mips = mips;
        this.ram = ram;
        this.storage = storage;
        this.cloudletList = new ArrayList<>();
        this.timeQuantum = timeQuantum;
    }

    public void assignCloudlets(List<Cloudlet> cloudlets) {
        this.cloudletList = new ArrayList<>(cloudlets);
    }

    @Override
    public void run() {
        System.out.println("\nVM " + id + " executing cloudlets using **Round-Robin Scheduling** (Time Quantum = " + timeQuantum + ")");
        double currentTime = 0;
        Queue<Cloudlet> queue = new LinkedList<>(cloudletList);

        while (!queue.isEmpty()) {
            Cloudlet cloudlet = queue.poll();
            if (cloudlet.startTime == 0) {
                cloudlet.startTime = currentTime;
            }

            int executionTime = Math.min(timeQuantum, cloudlet.remainingLength / mips);
            cloudlet.remainingLength -= executionTime * mips;
            currentTime += executionTime;
            totalExecutionTime += executionTime;

            if (cloudlet.remainingLength > 0) {
                queue.add(cloudlet);
            } else {
                cloudlet.finishTime = currentTime;
                double turnaroundTime = cloudlet.finishTime - cloudlet.arrivalTime;
                double executionTimeTaken = cloudlet.finishTime - cloudlet.startTime;
                System.out.println("Cloudlet " + cloudlet.id +
                        " executed on VM " + id +
                        " | Start Time: " + cloudlet.startTime +
                        " | Finish Time: " + cloudlet.finishTime +
                        " | Turnaround Time: " + turnaroundTime +
                        " | Execution Time: " + executionTimeTaken);
            }
        }
    }
}

public class RoundRobinMultiVM {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input Number of VMs
        System.out.print("Enter the number of VMs: ");
        int vmCount = scanner.nextInt();
        List<VM> vmList = new ArrayList<>();

        // Input Time Quantum for Round-Robin
        System.out.print("Enter the Time Quantum (in milliseconds): ");
        int timeQuantum = scanner.nextInt();

        // Input VM Details
        for (int i = 0; i < vmCount; i++) {
            System.out.print("\nEnter VM " + (i + 1) + " ID: ");
            int id = scanner.nextInt();
            System.out.print("Enter MIPS: ");
            int mips = scanner.nextInt();
            System.out.print("Enter RAM (MB): ");
            int ram = scanner.nextInt();
            System.out.print("Enter Storage (GB): ");
            int storage = scanner.nextInt();

            vmList.add(new VM(id, mips, ram, storage, timeQuantum));
        }

        // Input Number of Cloudlets
        System.out.print("\nEnter the number of Cloudlets: ");
        int cloudletCount = scanner.nextInt();
        List<Cloudlet> cloudlets = new ArrayList<>();

        // Input Cloudlet Details
        for (int i = 0; i < cloudletCount; i++) {
            System.out.print("\nEnter Cloudlet " + (i + 1) + " ID: ");
            int id = scanner.nextInt();
            System.out.print("Enter Length (MIs): ");
            int length = scanner.nextInt();

            cloudlets.add(new Cloudlet(id, length, 0)); // Arrival time is 0 (all Cloudlets arrive at the start)
        }

        // Distribute Cloudlets to VMs in Round-Robin
        for (int i = 0; i < cloudlets.size(); i++) {
            vmList.get(i % vmList.size()).cloudletList.add(cloudlets.get(i));
        }

        // Execute Cloudlets on Multiple VMs in Parallel
        long startTime = System.currentTimeMillis();
        for (VM vm : vmList) {
            vm.assignCloudlets(vm.cloudletList);
            vm.start();
        }

        // Wait for all VMs to finish execution
        for (VM vm : vmList) {
            try {
                vm.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Calculate Throughput
        long endTime = System.currentTimeMillis();
        double totalExecutionTime = (endTime - startTime) / 1000.0; // Convert to seconds
        double throughput = cloudletCount / totalExecutionTime;
        System.out.println("\nTotal Execution Time: " + totalExecutionTime + " seconds");
        System.out.println("Throughput: " + throughput + " Cloudlets per second");

        scanner.close();
    }
}
