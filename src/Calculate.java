import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Calculate {

    private List<Slope> slopes=new LinkedList<>();
    private List<Double> list=new LinkedList<>();

    public void addEye(double x)
    {
        list.add(x);
    }

    public void processEye()
    {
        getExtremePoint();//找局部极值点，在列表slopes中添加斜率
        System.out.println("*************寻找所有斜率************");
        displaySlopes(slopes);

        List<Slope> slopes_distribution=getSlopeByDistribution(slopes);//计算分布，选择分布最多的区间（这里分为3个区间）
        System.out.println("*************寻找最大分布斜率************");
        displaySlopes(slopes_distribution);

        List<Slope> slopes_sortd=getSlopesBySort(slopes_distribution);//将选中的区间内的线段按照帧数从大到小排序
        System.out.println("*************寻找排序后的斜率************");
        displaySlopes(slopes_sortd);

        List<Slope> slopes_removeMax=getSlopesByRemoveMax((slopes_sortd));//选择前9个线段，并去掉其中斜率的最大值
        System.out.println("*************寻找去除掉最大值的斜率************");
        displaySlopes(slopes_removeMax);

        
        double slope_average=getAverage(slopes_removeMax);//求其余平均值
        System.out.println(slope_average);
    }

    //找局部极值点
    private void getExtremePoint()
    {
        boolean findMax = true;
        int len=list.size();
        double pre=list.get(0);//上一个极值点
        double cur;//当前极值点
        for(int i=0;i<len;i++)
        {
            double x=list.get(i);
            //寻找极大值与极小值
            int j=findMaxOrMin(list,i,findMax);
            cur=list.get(j);
            Slope slope=new Slope(i,pre,j,cur,true);
            slopes.add(slope);
            findMax=!findMax;
            if(j==len-1)
            {
                break;
            }
            i=j-1;
        }
    }

    private int findMaxOrMin(List<Double> list,int index,boolean max)
    {
        int len = list.size();
        double pre = list.get(index);
        double cur;
        for(int i=index+1;i<len;i++)
        {
            cur=list.get(i);
            if(cur<pre==max)//当前值与上一个值比较
            {
                //当前值比上一个值小，证明上一个值是极大值
                return i-1;
            }
            pre=cur;
        }
        return len-1;
    }


    //计算分布
    private List<Slope> getSlopeByDistribution(List<Slope> slopes)
    {
        int space=3;
        //按照从小到大排序
        slopes.sort(new Comparator<Slope>() {
            @Override
            public int compare(Slope o1, Slope o2) {
                return Double.compare(o1.getSlope(), o2.getSlope());
            }
        });
        Slope max=slopes.get(slopes.size()-1);
        Slope min=slopes.get(0);
        double range=(max.getSlope()-min.getSlope())/space;
        int[] k=new int[space];
        List<List<Slope>> centerSlopes=new ArrayList<>();
        //初始化
        for(int i=0;i<k.length;i++)
        {
            centerSlopes.add(new LinkedList<>());
        }
        for(Slope slope:slopes)
        {
            int i=(int)Math.floor((slope.getSlope()-min.getSlope())/range);
            if(i==k.length)
            {
                //最大值
                i--;
            }
            k[i]++;
            centerSlopes.get(i).add(slope);
        }
        //找出最大的一组
        int index=0;
        int maxNum=0;
        for(int i=0;i<k.length;i++)
        {
            if(k[i]>maxNum)
            {
                maxNum=k[i];
                index=i;
            }
        }
        return centerSlopes.get(index);
    }

    //将选中的区间内的线段按照帧数从大到小排序
    private List<Slope> getSlopesBySort(List<Slope> slopes)
    {
        if(slopes==null||slopes.size()<=1)
        {
            return null;
        }
        slopes.sort(new Comparator<Slope>() {
            @Override
            public int compare(Slope o1, Slope o2) {
                if(o1.getSize()<o2.getSize())
                {
                    return -1;
                }
                return 1;
            }
        });
        return slopes;
    }

    //选择前9个线段，并去掉其中斜率的最大值
    private List<Slope> getSlopesByRemoveMax(List<Slope> slopes)
    {
        if(slopes==null||slopes.size()<=1)
        {
            return null;
        }
        //选取前九个线段
        List<Slope> slopes_temp=slopes.subList(0,9);
        //按照从小到大排序
        slopes_temp.sort(new Comparator<Slope>() {
            @Override
            public int compare(Slope o1, Slope o2) {
                return Double.compare(o1.getSlope(), o2.getSlope());
            }
        });
        slopes_temp.remove(slopes_temp.size() - 1);//去掉最大值
        return slopes_temp;
    }

    //求其余平均值
    private double getAverage(List<Slope> slopes)
    {
        if(slopes==null||slopes.size()==0)
        {
            return 0;
        }
        double sum=0;
        for(Slope slope:slopes)
        {
            sum+=slope.getSlope();
        }
        return sum/slopes.size();
    }

    private void displaySlopes(List<Slope> slopes)
    {
        if(slopes==null)
        {
            return;
        }
        for(Slope slope:slopes)
        {
            System.out.println(slope);
        }
    }

}
