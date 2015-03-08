/**
 * @author : Adithya Addanki (aa207)
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

public class UnionFind {
	
/* Constructor for Initializing the file as Site to be checked*/	
UnionFind(String filePath){
	try{
		inFile= new Scanner(new FileReader(filePath));
	}
	catch(FileNotFoundException e)
	{
		System.out.println("Please check the file given as argument");
		System.out.print("Input file name given: "+filePath);
		System.exit(0);
	}
}

/* Constructor for Initializing the # of runs and other arguments*/
UnionFind(String args[]){
	this.args=args;
	r=new Random(8);
}

private String[] args;
private Scanner inFile;
private Random r;
static private int fileNum=0;

/* Point of entry for the Union Find Algorithm */
public static void main(String ar[])
{
	try{
		/* case for p,n,size */
		if(ar.length==3)
		{
			UnionFind uf=new UnionFind(ar);
			uf.percolcationRate();
		}
		/* case for checking # of clusters */
		else if(ar.length==1)
		{
			UnionFind uf=new UnionFind(ar[0]);
			uf.buildClusters();
		}
		else
			throw new Exception("Illegal usage");
	}
	catch(Exception e)
	{
		/* Help for any issues*/
		System.out.println(e.getMessage());
		System.out.println("Proper usage instructions: ");
		System.out.println("java UnionFind p n s | p-prob[0-1) ,n-# of runs, s-size");
		System.out.println("java UnionFind file | file-input file path");
		System.exit(0);
	}
}

/* Build the clusters after reading the Sites that are randomly generated for Case 1 */
/* Build the clusters after reading the site for case 2 */
/* Returns if the site percolates or not*/
private int buildClusters() throws IOException
{
	ArrayList<Node> site=new ArrayList<Node>();
	while(inFile.hasNext("[0-9]"))
	{
		site.add(new Node(inFile.nextInt()));
	}
	int boardLength=0;
	int hClus=-1;
	for(;boardLength*boardLength!=site.size();boardLength++);
	for(int i=0;i<site.size();i++)
	{
		int row=i/boardLength;
		int col=i%boardLength;
		int topInd=-1;
		int leftInd=-1;
		int currInd=(row)*boardLength + col;
		Node curr=null,top=null,left=null;
		curr=site.get(row*boardLength + col);
		if(row!=0)
		{
			topInd=(row-1)*boardLength + col;
			top=site.get(topInd);
		}
		if(col!=0)
		{
			leftInd=row*boardLength + (col-1);
			left=site.get(leftInd);
		}
		if(left!=null && isConnected(curr.nodeVal,left.nodeVal))
		{
			int root1=find(site,currInd);
			int root2=find(site,leftInd);
			if(root1==root2 && root1==-1)
			{
				site.set(currInd,new Node(curr.nodeVal,leftInd,curr.size));
				site.set(leftInd,new Node(left.nodeVal,left.clusterId,curr.size+left.size));
			}
			else if(root1!=root2)
			{
				if(root2==-1)
				{
					root2=leftInd;
				}
				site.set(currInd, new Node(curr.nodeVal, root2,curr.size));
				site.set(root2, new Node(site.get(root2).nodeVal, site.get(root2).clusterId,curr.size+site.get(root2).size));
			}
			curr=site.get(row*boardLength + col);
		}
		if(top!=null && isConnected(curr.nodeVal,top.nodeVal))
		{
			int root1=find(site,topInd);
			int root2=find(site,currInd);
			if(root1==root2 && root1==-1)
			{
				site.set(currInd,new Node(curr.nodeVal,topInd,curr.size));
				site.set(topInd,new Node(top.nodeVal,top.clusterId,curr.size+top.size));
			}
			else if(root1!=root2)
			{
				if(root1==-1)
				{
					root1=topInd;
				}
				if(curr.clusterId!=-1)
				{
					int lroot=find(site,leftInd);
					if(lroot==-1)
					{
						lroot=leftInd;
					}
					if(site.get(lroot).size <= site.get(root1).size)
					{
						Node lr=site.get(lroot);
						site.set(lroot, new Node(lr.nodeVal,root1,lr.size));
						site.set(root1, new Node(site.get(root1).nodeVal,site.get(root1).clusterId,lr.size+site.get(root1).size));
					}
					else if(site.get(lroot).size > site.get(root1).size)
					{
						Node rr=site.get(root1);
						site.set(root1, new Node(rr.nodeVal,lroot,rr.size));
						site.set(lroot, new Node(site.get(lroot).nodeVal,site.get(lroot).clusterId,site.get(lroot).size+rr.size));
					}
				}
				else
				{
					site.set(currInd, new Node(curr.nodeVal,root1,curr.size));
					site.set(root1, new Node(site.get(root1).nodeVal,site.get(root1).clusterId,curr.size+site.get(root1).size));
				}
			}
		}
		curr=site.get(row*boardLength + col);
		if(curr.clusterId>hClus)
		{
			hClus=curr.clusterId;
		}
		//if(i%boardLength==0)
			//System.out.println();
	}
	//printClusters(site, boardLength);
	site=renameClusters(site,hClus);
	//System.out.println("-------------------------------");
	if(printClusters(site, boardLength))
	{
		return 1;
	}
	else 
		return 0;
}
/* Prints the clusters and checks for percolation, helper method for buildClusters */
/* Inputs: Site and board Length */
/* Outputs: percolation possibility */
private boolean printClusters(ArrayList<Node> site,int boardLength) throws IOException
{
	
	HashSet<Integer> hs= new HashSet<Integer>();
	for(int i=0;i<site.size();i++)
	{
		if(site.get(i).clusterId!=-1)
			hs.add(site.get(i).clusterId);
//		System.out.print((site.get(i).clusterId)+"\t");
//		if((i+1)%boardLength==0)
//			System.out.println();
	}
	System.out.println("No of Clusters: "+ hs.size());
	boolean percCheck=doesPercolate(site,hs,boardLength);
	System.out.println("Does the site percolate? : "+(percCheck?"Yes": "No"));
	return percCheck;
}

/* Creates a PPM for the input site along with color codings */
/* Inputs: Site and board Length and cluster ids */
/* Outputs: percolation possibility coded in the PPM as white colored flow from top to bottom */
private boolean doesPercolate(ArrayList<Node> site,HashSet<Integer> hs,int boardLength)throws IOException 
{
	PrintWriter pw =new PrintWriter(new FileWriter("gameBoard"+(fileNum++)+".ppm"));
	pw.println("P3");
	pw.println("#Game board: "+ "gameBoard"+(fileNum));
	pw.println(boardLength+" "+boardLength);
	pw.println("255");
	int priClus=-1;
	boolean perc=false;
	ArrayList<Integer> hsCp= new ArrayList<Integer>();
	hsCp.addAll(hs);
	hsCp.sort(null);
	//System.out.println(hsCp);
	for(int is=0;is<hsCp.size() && hsCp.size()>0;is++)
	{
		for(int i=0;i<boardLength ;i++)
		{
			int clus=site.get(i).clusterId;
			if(clus!=-1 && clus==hsCp.get(is))
				{
					priClus=clus;
					break;
				}
		}
		for(int j=site.size()-1;j>site.size()-boardLength-1;j--)
		{
			int clus=site.get(j).clusterId;
				if(priClus==clus)
				{
					perc= true;
					break;
				}
		}
		if(perc==true)
			break;
	}
	for(int rgb=0;rgb<site.size();rgb++)
	{
		int x= site.get(rgb).clusterId;
		if(x==priClus)
		{
			pw.print("255 255 255 ");
		}
		else if(x==-1){
			pw.print(0+" "+0 +" "+0+" ");
		}
		else
		{
			x=x % 255;
			pw.print(x+" "+x+" "+x+" ");
		}
		
		if((rgb+1)%boardLength==0)
			pw.println();
	}
	pw.close();
	return perc;
}

/* Unifies the cluster Ids for path compression*/
/* Inputs : site and highest cluster id*/
/* Outputs : unified clusters */
private ArrayList<Node> renameClusters(ArrayList<Node> site, int hclus)
{
	for(int ind=site.size()-1;ind>=0;ind--)
	{
		Node temp=site.get(ind);
		int root=find(site,ind);
		if(root==-1 && temp.nodeVal==1)
		{
			site.set(ind, new Node(temp.nodeVal,ind,temp.size));
		}
		else if(temp.nodeVal==1 && temp.clusterId!=-1){
			site.set(ind, new Node(temp.nodeVal,root,temp.size));
		}
		else if(temp.clusterId==-1 && temp.nodeVal==1)
		{
			site.set(ind, new Node(temp.nodeVal,hclus++,temp.size));
		}
		else if(temp.nodeVal==0)
			continue;
	}
	return site;
}

/* Helper method for checking connectivity between open sites */
/* Inputs: node val1, val2 */
/* Outputs: connectivity */
private boolean isConnected(int val1,int val2)
{
	return (val1==val2) && (val1==1);
}

/* Helper method to find the root of the element in the sitemap */
/* Inputs: Sitemap and site 
 * Outputs: root of the site */
private int find(ArrayList<Node> al,int ele)
{
	int clus=al.get(ele).clusterId;
	int root=-1;
	while(clus!=-1)
	{
		root=clus;
		clus=al.get(clus).clusterId;
		if(clus==-1 || root==clus)
		{ 
			break;
		}
	}
	clus=root;
	return clus;
}

/* Helper method to generate random number*/
private int getRandomNum(int len){
	return r.nextInt(len);
}

/* Method to create sites for the case 1
 * Inputs : None
 * Outputs: sitemaps and percolation rate statistics for the run
 * */
private void percolcationRate() throws Exception
{
	float percProb=Float.parseFloat(args[0]);
	if(percProb>=1 || percProb <0)
		throw new Exception("Probability should be in the range of 0-1");
	FileWriter afw=new FileWriter("AllFiles.txt");
	PrintWriter apw=new PrintWriter(afw);
	int numofRuns=Integer.parseInt(args[1]);
	int boardSize=Integer.parseInt(args[2]);
	int[] board=new int[boardSize*boardSize];
	int threshold = (int)(Math.ceil(percProb*board.length));
	String[] fileNames=new String[numofRuns];
	double percolationRate=0;
	for(int i=0;i<numofRuns;i++)
	{
		int j=0;
		alreadyOne:
		while(j<threshold)
		{
			int rand=getRandomNum(board.length);
			if(board[rand] == 1)
			{
				continue alreadyOne;
			}
			else
			{
				board[rand]=1;
				++j;
			}
		}
		fileNames[i]="genSites"+(i+1)+".txt";
		FileWriter fw=new FileWriter(fileNames[i]);
		PrintWriter pw=new PrintWriter(fw);
		for(int ind=0;ind<board.length;ind++)
		{
			pw.print(board[ind]+"\t");
			apw.print(board[ind]+"\t");
			if((ind+1)%boardSize==0)
			{
				pw.println();
				apw.println();
			}
		}
		apw.println();
		fw.close();
		board=new int[boardSize*boardSize];
	}
	afw.close();
	for(int prC=0;prC<numofRuns;prC++)
	{
	UnionFind uf= new UnionFind(fileNames[prC]);
	int pc=uf.buildClusters();
	percolationRate+=pc;
	System.out.println("**********");
	}
	System.out.println();
	System.out.println("********************************");
	System.out.println("Statistics for the sample run: ");
	System.out.println("********************************");
	System.out.println("Percolation rate with \nprobability p: "+percProb+", \nNo of Runs: "+ numofRuns+", \nBoard Size: "+boardSize);
	System.out.println("--> "+(percolationRate/numofRuns));
}
}

/* Node class for cluster ids, node values and size details*/
class Node
{
	int clusterId;
	int nodeVal;
	int size;
	Node(int val)
	{
		nodeVal=val;
		clusterId=-1;
		size=1;
	}
	Node(int val,int clus, int size)
	{
		nodeVal=val;
		clusterId=clus;
		this.size=size;
	}
}