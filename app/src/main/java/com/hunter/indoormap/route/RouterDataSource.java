package com.hunter.indoormap.route;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Tagger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hunter on 5/10/17.
 */

public interface RouterDataSource {

    class Wnode<E extends GPoint> {
        private E item;
        private List<Wnode<E>> next;

        public Wnode(E element) {
            this.item = element;
            this.next = new LinkedList<>();
        }

        public Wnode<E> addNext(Wnode<E> n) {
            if (!next.contains(n)) {
                next.add(n);
            }
            return this;
        }

        public Wnode<E> addNext(List<Wnode<E>> n) {
            for (Wnode wnode : n) {
                addNext(wnode);
            }
            return this;
        }

        public Wnode<E> removeNext(Wnode<E> n) {
            next.remove(n);
            return this;
        }

        public Wnode<E> clearNexts() {
            next.clear();
            return this;
        }

        public E getItem() {
            return item;
        }

        public void setItem(E item) {
            this.item = item;
        }

        public List<Wnode<E>> getNexts() {
            return Collections.unmodifiableList(next);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Wnode)) return false;

            Wnode<?> wnode = (Wnode<?>) o;

            return item != null ? item.equals(wnode.item) : wnode.item == null;

        }

        public List<E> getNextItems() {
            List<E> items = new LinkedList<>();
            for (Wnode<E> wnode : next) {
                items.add(wnode.item);
            }
            return items;
        }

        @Override
        public String toString() {
            return "Wnode{" +
                    "item=" + item +
                    ", next=" + getNextItems() +
                    '}';
        }
    }

    Wnode getWnode(GPoint gPoint);
}
