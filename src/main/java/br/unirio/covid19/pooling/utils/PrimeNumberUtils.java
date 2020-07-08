package br.unirio.covid19.pooling.utils;

/**
 * Class that contains utility math functions
 */
public class PrimeNumberUtils 
{
    public static int nextPrime(int number)
    {
        number++;

        while (!isPrime(number))
            number++;

        return number;
    }
    
    public static boolean isPrime(int number)
    {    
        if (number <= 1) 
            return false;  

        for (int i = 2; i <= Math.sqrt(number); i++)
            if (number % i == 0)
                return false;  

        return true;  
    }    
}