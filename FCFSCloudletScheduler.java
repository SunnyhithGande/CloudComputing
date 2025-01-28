import java.util.List;
import java.util.ArrayList;
class Cloudlet 
{
    int id;
    int length;
    int fileSize;
    int outputSize;
    int numCpus;
    int vmId;
    public Cloudlet(int id, int length, int fileSize, int outputSize, int numCpus) 
    {
        this.id = id;
        this.length = length;
        this.fileSize = fileSize;
        this.outputSize = outputSize;
        this.numCpus = numCpus;
        this.vmId = -1;
    }
}
class VM
{
    int id;
    int mips;
    int ram;
    int bw;
    int numCpus;
    public VM(int id, int mips, int ram, int bw, int numCpus) 
    {
        this.id = id;
        this.mips = mips;
        this.ram = ram;
        this.bw = bw;
        this.numCpus = numCpus;
    }
}
public class FCFSCloudletScheduler 
{
    List<Cloudlet> cloudlets;
    List<VM> vms;
    private double totalExecutionTime = 0;
    private double totalTurnaroundTime = 0;
    private double totalWaitingTime = 0;
    private double totalResponseTime = 0;
    public FCFSCloudletScheduler() 
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
        int vmIndex = 0;
        for (Cloudlet cloudlet : cloudlets) 
        {
            cloudlet.vmId = vms.get(vmIndex).id;
            vmIndex = (vmIndex + 1) % vms.size();
        }
    }
    public void executeAndPrintResults(int cloudletCount) throws InterruptedException
    {
        List<Thread> threads = new ArrayList<>();
        for (Cloudlet cloudlet : cloudlets) 
        {
            VM vm = vms.get(cloudlet.vmId);
            Thread thread = new Thread(() -> 
            {
                double executionTime = (double) cloudlet.length / vm.mips;
                synchronized (this) {
                    totalExecutionTime += executionTime;
                    totalWaitingTime += totalExecutionTime;
                    totalTurnaroundTime += totalExecutionTime;
                    totalResponseTime += totalWaitingTime;
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) 
        {
            thread.join();
        }
        double avgTurnaroundTime = totalTurnaroundTime / cloudletCount;
        double throughput = cloudletCount / totalExecutionTime;
        System.out.printf("%-15d%-20.2f%-20.2f%-20.2f\n", cloudletCount, avgTurnaroundTime, throughput, totalExecutionTime);
    }
    public static void main(String[] args) throws InterruptedException 
    {
        FCFSCloudletScheduler scheduler = new FCFSCloudletScheduler();
        VM vm1 = new VM(0, 1000, 1024, 1000, 2);
        VM vm2 = new VM(1, 1200, 1024, 1000, 2);
     scheduler.addVm(vm1);
        scheduler.addVm(vm2);
        int[] cloudletCounts = {5, 10, 15, 20, 25};
        System.out.printf("%-15s%-20s%-20s%-20s\n", "Cloudlet Count", "Avg. TAT", "Throughput", "Total Exec Time");
        for (int cloudletCount : cloudletCounts) 
        {
            scheduler.cloudlets.clear();
            for (int i = 0; i < cloudletCount; i++) 
            {
                Cloudlet cloudlet = new Cloudlet(i, (i + 1) * 1000, 300, 300, 2);
                scheduler.addCloudlet(cloudlet);
            }
            scheduler.assignCloudletsToVms();
            scheduler.totalExecutionTime = 0;
            scheduler.totalTurnaroundTime = 0;
            scheduler.totalWaitingTime = 0;
            scheduler.totalResponseTime = 0;
            scheduler.executeAndPrintResults(cloudletCount);
        }
    }
}
