package Problem1;

import java.util.concurrent.locks.ReentrantLock;

public class OwnList {
    private class ListNode {
        int val;
        ListNode nextNode;
        ReentrantLock lock = new ReentrantLock();

        ListNode(int val) {
            this.val = val;
        }
    }

    private final ListNode minIntegerNode = new ListNode(Integer.MIN_VALUE);

    public void insert(int val) {
        ListNode previous = minIntegerNode;
        try {
            previous.lock.lock();
            ListNode current = previous.nextNode;
            while (current != null && current.val < val) {
                current.lock.lock();
                previous.lock.unlock();
                previous = current;
                current = current.nextNode;
            }
            if (current == null || current.val != val) {
                ListNode newNode = new ListNode(val);
                previous.nextNode = newNode;
                System.out.println("Inserted node with val: " + val);
            }
        } finally {
            previous.lock.unlock();
        }
    }

    public void delete(int val) {
        ListNode previous = minIntegerNode;
        try {
            previous.lock.lock();
            ListNode current = previous.nextNode;
            while (current != null && current.val != val) {
                current.lock.lock();
                previous.lock.unlock();
                previous = current;
                current = current.nextNode;
            }
            if (current != null && current.val == val) {
                current.lock.lock();
                previous.nextNode = current.nextNode;
                current.lock.unlock();
                System.out.println("Deleted value from List: " + current.val);
            }
        } finally {
            previous.lock.unlock();
        }
    }
}
