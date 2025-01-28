import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Cloudlet 
{
    int id;
    int length;
    int fileSize;
    int outputSize;
    int numCpus;
    Integer vmId; 

    public Cloudlet(int id, int length, int fileSize, int outputSize, int numCpus) 
    {
        this.id = id;
        this.length = length;
        this.fileSize = fileSize;
        this.outputSize = outputSize;
        this.numCpus = numCpus;
        this.vmId = null;
    }
}

class VM 
{
    int id;
    int mips;
    int ram;
    int bw;
    int numCpus;
    List<Cloudlet> cloudlets;
    double energyConsumption;

    public VM(int id, int mips, int ram, int bw, int numCpus) 
    {
        this.id = id;
        this.mips = mips;
        this.ram = ram;
        this.bw = bw;
        this.numCpus = numCpus;
        this.cloudlets = new ArrayList<>();
        this.energyConsumption = 0;
    }
}

class FCFSScheduler 
{
    List<Cloudlet> cloudlets;
    List<VM> vms;
    public FCFSScheduler() 
    {
        this.cloudlets = new ArrayList<>();
        this.vms = new ArrayList<>();
    }

    public void addVm(VM vm) 
    {
        vms.add(vm);
    }

    public void addCloudlet(Cloudlet cloudlet) 
    {
        cloudlets.add(cloudlet);
    }

    public void assignCloudletsToVms() 
    {
        int vmCount = vms.size();
        if (vmCount == 0) {
            throw new IllegalArgumentException("No VMs available for cloudlet assignment.");
        }

        
        for (int i = 0; i < cloudlets.size(); i++) 
        {
            Cloudlet cloudlet = cloudlets.get(i);
            VM vm = vms.get(i % vmCount); 
            vm.cloudlets.add(cloudlet);
            cloudlet.vmId = vm.id;
        }
    }

    public void printResults(List<Integer> cloudletCounts) 
    {
        System.out.printf("%-20s%-20s%-20s%-20s\n", "Cloudlet Count", "Avg. TAT", "Throughput", "Total Execution Time");

        for (int cloudletCount : cloudletCounts) 
        {
            List<VM> vms = new ArrayList<>();
            for (int i = 0; i < 1; i++) 
            {
                vms.add(new VM(i, 500, 512, 1000, 1));
            }
            List<Cloudlet> cloudlets = new ArrayList<>();
            Random rand = new Random();
            for (int i = 0; i < cloudletCount; i++)
            {
                cloudlets.add(new Cloudlet(i, rand.nextInt(4000) + 1000, 300, 300, 1));
            }
            FCFSScheduler scheduler = new FCFSScheduler();
            for (VM vm : vms) 
            {
                scheduler.addVm(vm);
            }

            for (Cloudlet cloudlet : cloudlets) 
            {
                scheduler.addCloudlet(cloudlet);
            }
            scheduler.assignCloudletsToVms();
            double[] results = scheduler.execute();
            double totalExecutionTime = results[0];
            double totalWaitingTime = results[1];
            double totalTurnaroundTime = results[2];
            double totalResponseTime = results[3];
            double totalEnergyConsumption = results[4];

            double throughput = cloudletCount / totalExecutionTime;
            double avgTurnaroundTime = totalTurnaroundTime / cloudletCount;
            System.out.printf("%-20d%-20.2f%-20.2f%-20.2f\n", cloudletCount, avgTurnaroundTime, throughput, totalExecutionTime);
        }
    }

    private double[] execute() 
    {
        double totalExecutionTime = 0;
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        double totalResponseTime = 0;
        double totalEnergyConsumption = 0;

        for (VM vm : vms) 
        {
            double executionTimeVm = 0;
            for (Cloudlet cloudlet : vm.cloudlets) 
            {
                double executionTime = (double) cloudlet.length / vm.mips;
                double responseTime = executionTimeVm;
                double waitingTime = responseTime;
                double turnaroundTime = waitingTime + executionTime;
                double energy = executionTime * vm.mips;
                totalExecutionTime += executionTime;
                totalWaitingTime += waitingTime;
                totalTurnaroundTime += turnaroundTime;
                totalResponseTime += responseTime;
                totalEnergyConsumption += energy;

                executionTimeVm += executionTime;
            }
        }

        return new double[]{totalExecutionTime, totalWaitingTime, totalTurnaroundTime, totalResponseTime, totalEnergyConsumption};
    }
}


public class SingleVirtualMachine
{
    public static void main(String[] args) 
    {
        List<Integer> cloudletCounts = List.of(5, 10, 15, 20, 25, 30);
        FCFSScheduler scheduler = new FCFSScheduler();
        scheduler.printResults(cloudletCounts);
    }
}  