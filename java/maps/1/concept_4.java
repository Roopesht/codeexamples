{
  "cells": [
    {
      "cell_type": "markdown",
      "metadata": {},
      "source": [
        "# Iterating Through Maps in Java\n",
        "\n",
        "This Java program demonstrates different ways to iterate over a Map (HashMap) in Java. It shows how to loop through keys, values, and entries (key-value pairs). The code includes explanations suited for beginners, with comments to help understand each part."
      ]
    },
    {
      "cell_type": "code",
      "execution_count": null,
      "metadata": {},
      "outputs": [],
      "source": [
        "import java.util.HashMap;\n",
        "import java.util.Map;\n",
        "\n",
        "public class MapIterationExample {\n",
        "    public static void main(String[] args) {\n",
        "        // Create a HashMap to store product names and their prices\n",
        "        HashMap<String, Integer> productPrices = new HashMap<>();\n",
        "\n",
        "        // Add some products with their prices\n",
        "        productPrices.put(\"Laptop\", 800);  // Product: Laptop, Price: $800\n",
        "        productPrices.put(\"Phone\", 400);   // Product: Phone, Price: $400\n",
        "        productPrices.put(\"Tablet\", 300);  // Product: Tablet, Price: $300\n",
        "\n",
        "        // 1. Iterate over the keys of the map\n",
        "        System.out.println(\"=== Iterating Keys ===\");\n",
        "        for (String product : productPrices.keySet()) {\n",
        "            // Get the price using the key\n",
        "            Integer price = productPrices.get(product);\n",
        "            // Print the product name and its price\n",
        "            System.out.println(product + \" costs $\" + price);\n",
        "        }\n",
        "\n",
        "        // 2. Iterate over the values only\n",
        "        System.out.println(\"=== Iterating Values ===\");\n",
        "        int total = 0; // To store total sum of all prices\n",
        "        for (Integer price : productPrices.values()) {\n",
        "            total += price; // Add each price to total\n",
        "        }\n",
        "        // Display the total value of all products\n",
        "        System.out.println(\"Total value: $\" + total);\n",
        "\n",
        "        // 3. Iterate over entries (recommended way)\n",
        "        System.out.println(\"=== Iterating Entries ===\");\n",
        "        for (Map.Entry<String, Integer> entry : productPrices.entrySet()) {\n",
        "            // Get the key (product name) and value (price) from the entry\n",
        "            String product = entry.getKey();\n",
        "            Integer price = entry.getValue();\n",
        "            // Print the product name and its price\n",
        "            System.out.println(product + \" → $\" + price);\n",
        "        }\n",
        "    }\n",
        "}"
      ]
    }
  ],
  "metadata": {
    "kernelspec": {
      "display_name": "Java",
      "language": "java",
      "name": "java"
    },
    "language_info": {
      "name": "java"
    }
  },
  "nbformat": 4,
  "nbformat_minor": 5
}