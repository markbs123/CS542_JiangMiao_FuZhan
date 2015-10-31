package Bplustreeexp;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 
 * BPlusTree Class Assumptions: 
 * 1. No duplicate keys inserted 
 * 2. Order D: D<=number of keys in a node <=2*D 
 * 3. All keys are non-negative
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	/**
	 * get the value for a specific key
	 * @param key
	 * @return value
	 */
	public T get(K key) {  //get a specified key value
        if (root == null || key == null)
            return null;
        LeafNode NodeFound = (LeafNode)Search(root, key); // down casting from root

        T valueFound = null;
        for(int indx = 0; indx < NodeFound.keys.size(); indx++){
            if (key.compareTo((K) (NodeFound.keys.get(indx))) == 0){
                valueFound = (T)NodeFound.values.get(indx);
                System.out.println("111");
            }
        }
        System.out.println(valueFound);
        return valueFound;
        
	}

    private Node Search(Node<K, T> node, K searchKey) {
		if (node.isLeafNode) {
			return node;
		}
		IndexNode tmp = (IndexNode) node;// Now it is a index node
		if (searchKey.compareTo(node.keys.get(0)) < 0) // if the key wanted is less than the smallest key, search leftmost children
		{
			return Search((Node) (tmp.children.get(0)), searchKey);
		}
		if (searchKey.compareTo(node.keys.get(node.keys.size() - 1)) >= 0)// if the key wanted is more than the smallest key,search rightmost children
		{
			return Search((Node) (tmp.children.get(tmp.children.size() - 1)),
					searchKey);
		}
            // if the key wanted is between the leftmost and the rightmost key then using binary search
		int left = 0;
		int right = tmp.keys.size() - 1;
		while (left <= right) {
			int mid = (left + right) / 2;
			K tmpKey = (K) tmp.keys.get(mid);
			if (searchKey.compareTo(tmpKey) < 0) {
				right = mid - 1;
			}
			left = mid + 1;
		}
		return Search((Node) (tmp.children.get(left)), searchKey);
    }

    /**
	 * put a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void put(K key, T value) {
        Entry<K,Node<K,T>> entry = new AbstractMap.SimpleEntry<K,Node<K,T>>(key, new LeafNode<K, T>(key,value));
        if(root == null)
            root = entry.getValue();

        Entry<K,Node<K,T>> newChildEntry = putHelper(root,entry,null);

        if (newChildEntry == null)
            return;
            IndexNode newRoot = new IndexNode(newChildEntry.getKey(),root,newChildEntry.getValue());
            root = newRoot;
            return;
	}
    private Entry<K, Node<K,T>> putHelper(Node<K, T> node,Entry<K, Node<K,T>> entry, Entry<K, Node<K,T>> newChildEntry){
        if (!node.isLeafNode){ // node is a index node
            IndexNode idx = (IndexNode)node;
            int i = 0;
            while (i < node.keys.size()){//choose  subtree
                if (entry.getKey().compareTo(node.keys.get(i)) < 0)
                    break;
                i++;
            }
            newChildEntry = putHelper((Node) idx.children.get(i), entry, newChildEntry); //recursively, insert entry
            if (newChildEntry == null) 
            {
                return newChildEntry;
            }
                int j = 0;
                while (j < idx.keys.size()){  // find the position where to insert
                    if (newChildEntry.getKey().compareTo(node.keys.get(j)) < 0)
                        break;
                    j++;
                }

                idx.insertSorted(newChildEntry,j);//insert the entry into this node at the specified index so that it still remains sorted

                if (!idx.isOverflowed()){ // the usual case, the node has space
                    newChildEntry = null;
                    return newChildEntry;
                } 
                //split the node using splitIndexNode
                    newChildEntry = splitIndexNode(idx);// smallest key value on N2 , pointer to N2  (splittingKey, Right) 204
                    if (idx == root){ // if N is the root, create new node with<pointer to this node, newchildentry> make the tree's root-node pointer point to the new node
                        IndexNode newRoot = new IndexNode(newChildEntry.getKey(),root,newChildEntry.getValue());
                        root = newRoot;
                        newChildEntry = null;
                        return newChildEntry;
                    }
                    return newChildEntry;
        } else { //node is a leaf node
            LeafNode lf = (LeafNode)node;
            LeafNode InsertLeaf = (LeafNode)entry.getValue();

            lf.insertSorted(entry.getKey(),InsertLeaf.values.get(0));

            if (!lf.isOverflowed()){// the usual case, the node has space
                newChildEntry = null;
                return newChildEntry;
            } //if the leaf is full, split the node using splitLeafNode
                newChildEntry = splitLeafNode(lf);  //newChildEntry<smallest key value on N2 , pointer to N2>  (splittingKey, rightNode) 174
                if (lf == root){    //if N is the root, create new node with<pointer to this node, newchildentry> make the tree's root-node pointer point to the new node
                    IndexNode newRoot = new IndexNode(newChildEntry.getKey(),lf,newChildEntry.getValue());
                    root = newRoot;
                    newChildEntry = null;
                    return newChildEntry;
                }
                return newChildEntry;
        }
    }

	/**
	 * Split a leaf node and return the new right node and the splitting key as an Entry<splittingKey, RightNode> 
	 * first D key values and D+1 pointers stay
	 * last D keys and D+1 pointers move to new node rightNode
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {

        ArrayList<K> Rkeys = new ArrayList<K>();//new rightkey
        ArrayList<T> RValues = new ArrayList<T>();//rightvalues
        K splittingKey = leaf.keys.get(D);//splittingKey is the minimum value of which  should also be copied up to parent

        while (leaf.keys.size() > D){//first D stay, last D move to new
            Rkeys.add(leaf.keys.get(D));
            leaf.keys.remove(D);
            RValues.add(leaf.values.get(D));
            leaf.values.remove(D);
        }

        LeafNode rightNode = new LeafNode(Rkeys, RValues); //since leaf node is a double-linked list, after splitting we need to change chain pointer
        LeafNode Tmp = leaf.nextLeaf;
        leaf.nextLeaf = rightNode;  // now nextLeaf point to the new rightNode, original node's following-sibling's previousLeaf point to rightNode
        leaf.nextLeaf.previousLeaf = rightNode;
        rightNode.previousLeaf = leaf;// for rightNode, its previousLeaf point to leaf, and nextLeaf point to original node's following-sibling
        rightNode.nextLeaf = Tmp;


        Entry<K,Node<K,T>> entry = new AbstractMap.SimpleEntry<K,Node<K,T>>(splittingKey, rightNode);//splittingKey :minimum value of N2,  copied up to parent
		return entry;
	}

	/**
	 * split an indexNode and return the new right node and the splitting key as an Entry<splittingKey, RightNode>
//	 * indexNode is full containing 2D keys and 2D + 1 pointers, and we have to
	 * add another index entry to account for a child split. We now have 2d + 1 keys and
	 * 2d+2 pointers, yielding two minimally full non-leaf nodes, each containing d keys and
	 * d+1 pointers, we choose the `middle' key and a pointer to the second non-leaf node 
	 * constitute an index entry that must be inserted into the parent of the split non-leaf node.
	 * @param index
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {

        K splittingKey = index.keys.get(D);// first to push the `middle' key and a pointer up to the parent of the split node
        index.keys.remove(D);

        ArrayList<K> Rkeys = new ArrayList<K>(); //new right key
        ArrayList<Node<K,T>> RightChildren = new ArrayList<Node<K, T>>();

        RightChildren.add(index.children.get(D+1));
        index.children.remove(D+1);

        while (index.keys.size() > D){//first D keys stay, last D keys move to new rihgt node, 
            Rkeys.add(index.keys.get(D));
            index.keys.remove(D);
            RightChildren.add(index.children.get(D + 1));//first D+1 pointers stay, last D pointers move to new rihgt node, 
            index.children.remove(D + 1);
        }

        IndexNode Right = new IndexNode(Rkeys, RightChildren);
        Entry<K,Node<K,T>> entry = new AbstractMap.SimpleEntry<K,Node<K,T>>(splittingKey, Right);//splittingKey has been pushed up to parent,not in N2
		return entry;
	}

	/**
	 * remove a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void remove(K key) {
		
        if (root == null)
            return;
        LeafNode NodeFound = (LeafNode)Search(root, key);
        if (NodeFound == null)// if not find then do not remove
            return;
        Entry<K,Node<K,T>> entry = new AbstractMap.SimpleEntry<K,Node<K,T>>(key,NodeFound);
        Entry<K,Node<K,T>> oldChildEntry = removeHelper(root,root,entry,null);
        if (oldChildEntry == null) {
            if (root.keys.size() == 0){// if no root key, set children as the root
                if (!root.isLeafNode)
                    root = (Node)((IndexNode)root).children.get(0);
            }
            return;
        } 
            int i = 0;
            while (i < root.keys.size()){// find if oldchildentry key can be find in root, if not then return, if yes remove it
                if (oldChildEntry.getKey().compareTo(root.keys.get(i)) == 0)
                    break;
                i++;
            }
            if (i == root.keys.size())
                return;
            root.keys.remove(i);
            ((IndexNode)root).children.remove(i+1);
            return;
	}
    private Entry<K, Node<K,T>> removeHelper(Node<K,T> parentNode,Node<K,T> node,Entry<K, Node<K,T>> entry,Entry<K, Node<K,T>> oldChildEntry) {
        if (!node.isLeafNode) { // node is not leafnode
            IndexNode idx = (IndexNode) node;
            int i = 0;
            while (i < idx.keys.size()) { // choose subtree
                if (entry.getKey().compareTo(node.keys.get(i)) < 0)
                    break;
                i++;
            }
            oldChildEntry = removeHelper(idx, (Node) idx.children.get(i), entry, oldChildEntry); //recursive remove
            if (oldChildEntry == null) {// usual case, child not removed
                return oldChildEntry;
            } 
            // we discarded child node 
                int j = 0;
                while (j < idx.keys.size()){ //remove oldchildentry from node then check minimum occupancy
                    if (oldChildEntry.getKey().compareTo((K)idx.keys.get(j)) == 0)
                        break;
                    j++;
                }
                idx.keys.remove(j);
                idx.children.remove(j+1);

                if (!idx.isUnderflowed() || idx.keys.size() == 0) {// node has entries to spare
                    oldChildEntry = null;
                    return oldChildEntry;
                } 
                // node has no entries to spare
                    if (idx == root)
                        return oldChildEntry;

                    K tmp =(K)idx.keys.get(0);
                    int k = 0;
                    while(k < parentNode.keys.size()){ //find which poniter in node's parent points to the node
                        if (tmp.compareTo(parentNode.keys.get(k)) < 0)
                            break;
                        k++;
                    }
                   	//judge if a sibling of this node has extra entries,if a sibling S has extra entries, 
                	//redistribute node and S,if a sibling S has no extra entries, merge node and S
                    int operation;
                    if (k > 0 && ((IndexNode)parentNode).children.get(k-1) != null){
 
                        operation = handleIndexNodeUnderflow((IndexNode)((IndexNode)parentNode).children.get(k-1),idx,(IndexNode) parentNode);//k-1 means left sibling of the node
                    } else {
                        operation = handleIndexNodeUnderflow(idx,(IndexNode)((IndexNode)parentNode).children.get(k+1),(IndexNode) parentNode);//k+1 means right sibling of the node
                    }

                    if (operation == -1) { //after redistribution,return value is -1
                        oldChildEntry = null;
                        return oldChildEntry;
                    } else {//after merging, return value is i-1 
                        oldChildEntry = new AbstractMap.SimpleEntry<K, Node<K, T>>(parentNode.keys.get(operation), parentNode);//curent entry in parent for right node of merging operation, the i-1  key
                        return oldChildEntry;
                    }
        } else { // node is leafnode
            LeafNode lf = (LeafNode) node;
            int i = 0;
            while (i < lf.keys.size()) {//remove oldchildentry from node then check minimum occupancy
                if (lf.keys.get(i).equals(entry.getKey()))
                    break;
                i++;
            }
            lf.keys.remove(i);
            lf.values.remove(i);

            if (!lf.isUnderflowed()) {// node has entries to spare
                oldChildEntry = null;
                return oldChildEntry;
            } // node has no entries to spare
                if (lf == root)
                    return oldChildEntry;

                int operation;
                if (lf.previousLeaf != null && ((K)lf.keys.get(0)).compareTo(parentNode.keys.get(0)) >= 0) {//judge if lf is the leftmost node, if yes use sibling lf.previousLeaf and if not use sibling lf.nextLeaf
                    operation = handleLeafNodeUnderflow(lf.previousLeaf, lf, (IndexNode) parentNode);
                } else {
                    operation = handleLeafNodeUnderflow(lf, lf.nextLeaf, (IndexNode) parentNode);
                }

                if (operation == -1) {//after redistribution,return value is -1
                    oldChildEntry = null;
                    return oldChildEntry;
                } else {//after merging, return value is i-1 
                    oldChildEntry = new AbstractMap.SimpleEntry<K, Node<K, T>>(parentNode.keys.get(operation), parentNode);//curent entry in parent for right node of merging operation, the i-1  key
                    return oldChildEntry;
                }
            }
    }

	/**
	 * Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         remove the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
        if (right.keys.size() + left.keys.size() > 2*D){// redistribution
            int i = 0;
            while (i < parent.keys.size()){// find entry in parent for node on right
                if (right.keys.get(0).compareTo(parent.keys.get(i)) < 0)
                    break;
                i++;
            }

            if (right.keys.size() > left.keys.size()){// move one key and value pair from right node to left node
                while (left.keys.size() < D){
                    left.keys.add(right.keys.get(0));
                    right.keys.remove(0);
                    left.values.add(right.values.get(0));
                    right.values.remove(0);
                }
            } else {
                while (left.keys.size() > D){// move one key value pair from left node to right node
                    right.keys.add(0,left.keys.get(left.keys.size()-1)); 
                    left.keys.remove(left.keys.size()-1);
                    right.values.add(0,left.values.get(left.values.size()-1));
                    left.values.remove(left.values.size()-1);
                }
            }

            parent.keys.set(i-1,right.keys.get(0));// replace key value in parent entry by new low-key value in right node

            return -1;

        } else {//merging
            int i = 0;
            while(i < parent.keys.size()){ //find entry in parent for node on right
                if (right.keys.get(0).compareTo(parent.keys.get(i)) < 0)
                    break;
                i++;
            }

            while (right.keys.size()>0){// move all entries from right node to left node
                left.keys.add(right.keys.get(0));
                right.keys.remove(0);
                left.values.add(right.values.get(0));
                right.values.remove(0);
            }
            if (right.nextLeaf != null)
                right.nextLeaf.previousLeaf = left; //set chain pointer
            left.nextLeaf = right.nextLeaf;

            return i - 1;
        }
	}

	/**
	 * Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param leftIndex
	 *            : the smaller node
	 * @param rightIndex
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         remove the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex, IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
        if (rightIndex.keys.size() + leftIndex.keys.size() >= 2*D){ //redistribution
            int i = 0;
            while (i < parent.keys.size()) {//find entry in parent for node on right
                if (rightIndex.keys.get(0).compareTo(parent.keys.get(i)) < 0)
                    break;
                i++;
            }

            if(leftIndex.keys.size() > rightIndex.keys.size()){
                while (leftIndex.keys.size() > D) {//move one key /pointer pair from left node to right node
                    rightIndex.keys.add(0, parent.keys.get(i - 1));
                    rightIndex.children.add(leftIndex.children.get(leftIndex.children.size()-1));
                    parent.keys.set(i-1,leftIndex.keys.get(leftIndex.keys.size()-1)); // there is a new low key value on the right node and therefore must replace the old key in the root which is the (i-1)th key
                    leftIndex.keys.remove(leftIndex.keys.size()-1);
                    leftIndex.children.remove(leftIndex.children.size()-1);
                }
            } else {
                while (leftIndex.keys.size() < D) {//move one key /pointer pair from right node to left node
                    leftIndex.keys.add(parent.keys.get(i-1));
                    leftIndex.children.add(rightIndex.children.get(0));
                    parent.keys.set(i-1,rightIndex.keys.get(0));// there is a new low key value on the right node and therefore must replace the old key in the root which is the (i-1)th key
                    rightIndex.keys.remove(0);
                    rightIndex.children.remove(0);
                }
            }

            return -1;

        } else { //merging
            int i = 0;
            while (i < parent.keys.size()){   //find entry in parent for node on right
                if (rightIndex.keys.get(0).compareTo(parent.keys.get(i)) < 0)
                    break;
                i++;
            }

            leftIndex.keys.add(parent.keys.get(i-1));  // parent last key less than rightIndex.keys.get(0)  is key(i-1) which should be pull down

            while (rightIndex.keys.size() > 0){//move all entries to node on left
                leftIndex.children.add(rightIndex.children.get(0));
                rightIndex.children.remove(0);
                leftIndex.keys.add(rightIndex.keys.get(0));
                rightIndex.keys.remove(0);
            }
            leftIndex.children.add(rightIndex.children.get(0));
            rightIndex.children.remove(0);

            return i - 1;
        }
	}

}
