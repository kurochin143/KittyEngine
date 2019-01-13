package KittyEngine.Container;

import java.util.ArrayList;

public class KArrayList<E> extends ArrayList<E> {

    public int lastIndex() {
        return size() - 1;
    }

    public E pop() {
        return remove(lastIndex());
    }

    /**
     * swap to the last index then pop it out reducing array size
     * this can increase efficiency but it will not preserve order
     * @return removed object
     * @TODO will this really increase efficiency?
     */
    public E removeSwap(int index) {
       int lastIndex = lastIndex();

       if (index != lastIndex)
       {
           set(index, get(lastIndex));
       }

       return pop();
    }

    /**
     * find the object then swap to the last index then pop it out reducing array size
     * this can increase efficiency but it will not preserve order
     * @return removed object
     * @TODO will this really increase efficiency?
     */
    public E removeSwap(E e) {
        int i = find(e);

        if (i == -1) {
            return null;
        }

        return removeSwap(i);
    }

    public int find(E e) {
        for (int i = 0; i < size(); ++i)
        {
            if (get(i) == e) {
                return i;
            }
        }

        return -1;
    }

    public int findByPredicate(FindPredicate findPredicate) {
        for (int i = 0; i < size(); ++i) {
            if (findPredicate.found(get(i))) {
                return i;
            }
        }

        return -1;
    }

    @FunctionalInterface
    public interface FindPredicate {
        boolean found(Object arrayObject);
    }

}
