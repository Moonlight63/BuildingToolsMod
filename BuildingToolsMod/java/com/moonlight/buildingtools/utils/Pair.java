package com.moonlight.buildingtools.utils;

public class Pair<K, V>
{

    /**
     * The first value.
     */
    private K key;
    /**
     * The second value.
     */
    private V value;

    /**
     * Creates a new part.
     * 
     * @param k the first value
     * @param v the second value
     */
    public Pair(K k, V v)
    {
        this.key = k;
        this.value = v;
    }

    /**
     * Returns the first value
     * 
     * @return the first
     */
    public K getKey()
    {
        return this.key;
    }

    /**
     * Returns the second value
     * 
     * @return the second
     */
    public V getValue()
    {
        return this.value;
    }
}
