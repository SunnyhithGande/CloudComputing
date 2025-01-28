import java.util.*;
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
    List<Cloudlet> assignedCloudlets;

    public VM(int id, int mips, int ram, int bw, int numCpus) 
    {
        this.id = id;
        this.mips = mips;
        this.ram = ram;
        this.bw = bw;
        this.numCpus = numCpus;
        this.assignedCloudlets = new ArrayList<>();
    }

    public void assignCloudlet(Cloudlet cloudlet)
    {
        assignedCloudlets.add(cloudlet);
        cloudlet.vmId = this.id;
    }
}

class FCFSScheduler {
    private List<Cloudlet> cloudlets;
    private List<VM> vms;

    public FCFSScheduler(List<VM> vms, List<Cloudlet> cloudlets) 
    {
        this.vms = vms;
        this.cloudlets = cloudlets;
    }

    public void schedule() 
    {
        int vmCount = vms.size();
        if (vmCount == 0) 
        {
            throw new IllegalStateException("No VMs available for scheduling.");
        }

        int vmIndex = 0;
        for (Cloudlet cloudlet : cloudlets) 
        {
            VM vm = vms.get(vmIndex % vmCount);
            vm.assignCloudlet(cloudlet);
            vmIndex++;
        }
    }

    public void printResults() 
    {
        System.out.println("FCFS Scheduling Results:");
        System.out.printf("%-10s%-10s%-10s\n", "VM ID", "Cloudlet ID", "Execution Time");
        for (VM vm : vms) {
            for (Cloudlet cloudlet : vm.assignedCloudlets) 
            {
                double executionTime = (double) cloudlet.length / vm.mips;
                System.out.printf("%-10d%-10d%-10.2f\n", vm.id, cloudlet.id, executionTime);
            }
        }
    }
}

public class FCFS
{
    public static void main(String[] args) 
    {
        List<VM> vms = new ArrayList<>();
        vms.add(new VM(0, 500, 1024, 1000, 1));
        vms.add(new VM(1, 500, 1024, 1000, 1));
        List<Cloudlet> cloudlets = new ArrayList<>();
        cloudlets.add(new Cloudlet(0, 2000, 300, 300, 1));
        cloudlets.add(new Cloudlet(1, 4000, 300, 300, 1));
        cloudlets.add(new Cloudlet(2, 3000, 300, 300, 1));
        cloudlets.add(new Cloudlet(3, 5000, 300, 300, 1));
        FCFSScheduler scheduler = new FCFSScheduler(vms, cloudlets);
        scheduler.schedule();
        scheduler.printResults();
    }
}
