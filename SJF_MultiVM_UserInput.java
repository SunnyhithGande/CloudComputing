import java.util.*;

class Cloudlet {
    int id, length;
    double turnaroundTime, waitingTime, finishTime, executionTime;

    public Cloudlet(int id, int length) {
        this.id = id;
        this.length = length;
    }
}

class VM extends Thread {
    int id, mips, ram, storage;
    List<Cloudlet> cloudletList;
    double totalExecutionTime = 0;

    public VM(int id, int mips, int ram, int storage) {
        this.id = id;
        this.mips = mips;
        this.ram = ram;
        this.storage = storage;
        this.cloudletList = new ArrayList<>();
    }

    public void assignCloudlets(List<Cloudlet> cloudlets) {
        this.cloudletList = new ArrayList<>(cloudlets);
    }

    @Override
    public void run() {
        System.out.println("\nVM " + id + " executing cloudlets using **SJF**...");
        double currentTime = 0;

        for (Cloudlet cloudlet : cloudletList) {
            cloudlet.executionTime = (double) cloudlet.length / mips;
            cloudlet.finishTime = currentTime + cloudlet.executionTime;
            cloudlet.turnaroundTime = cloudlet.finishTime;
            cloudlet.waitingTime = cloudlet.turnaroundTime - cloudlet.executionTime;

            System.out.println("Cloudlet " + cloudlet.id + 
                " executed on VM " + id +
                " | Execution Time: " + cloudlet.executionTime +
                " | Finish Time: " + cloudlet.finishTime +
                " | Turnaround Time: " + cloudlet.turnaroundTime +
                " | Waiting Time: " + cloudlet.waitingTime);

            totalExecutionTime += cloudlet.executionTime;
            currentTime += cloudlet.executionTime;
        }
    }
}

public class SJF_MultiVM_UserInput {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input Number of VMs
        System.out.print("Enter the number of VMs: ");
        int vmCount = scanner.nextInt();
        List<VM> vmList = new ArrayList<>();

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

            vmList.add(new VM(id, mips, ram, storage));
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

            cloudlets.add(new Cloudlet(id, length));
        }

        // Sort Cloudlets by Length (SJF)
        cloudlets.sort(Comparator.comparingInt(c -> c.length));

        // Distribute Cloudlets to VMs (Round-Robin)
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
        long endTime = System.currentTimeMillis();

        // Compute Metrics
        double totalExecutionTime = (endTime - startTime) / 1000.0;
        double throughput = cloudletCount / totalExecutionTime;

        double totalTurnaroundTime = cloudlets.stream().mapToDouble(c -> c.turnaroundTime).sum();
        double avgTurnaroundTime = totalTurnaroundTime / cloudletCount;

        double totalWaitingTime = cloudlets.stream().mapToDouble(c -> c.waitingTime).sum();
        double avgWaitingTime = totalWaitingTime / cloudletCount;

        System.out.println("\n📌 **Final Metrics:**");
        System.out.println("Total Execution Time: " + totalExecutionTime + " seconds");
        System.out.println("Throughput: " + throughput + " Cloudlets per second");
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
        System.out.println("Average Waiting Time: " + avgWaitingTime);

        scanner.close();
    }
}
