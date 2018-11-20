package com.smartsoft.csp.ssutil;

public class BitSet32 {

    public int word;

    public BitSet32() {
        word = 0;
    }

    public boolean get(int bitIndex) {
        return (word & (1 << bitIndex)) != 0;
    }

    public void set(int bit) {
        this.word |= (1 << bit);
    }

    public void clear(int bitIndex) {
        word &= ~(1L << bitIndex);

    }

    public boolean isSet(int bitIndex) {
        return get(bitIndex);
    }

    public boolean contains(int bitIndex) {
        return isSet(bitIndex);
    }

    public boolean add(int bitIndex) {
        if (contains(bitIndex)) {
            return false;
        } else {
            set(bitIndex);
            return true;
        }
    }

    public int asInt() {
        return word;
    }

    public void setWord(int word) {
        this.word = word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BitSet32)) return false;
        BitSet32 bitSet32 = (BitSet32) o;
        return word == bitSet32.word;
    }

    @Override
    public int hashCode() {
        return word;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 32; i++) {
            boolean value = this.get(i);
            if (value) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }

        return sb.toString();

    }


    public int getWord() {
        return word;
    }

    public int size() {
        return Integer.bitCount(word);
    }
}

