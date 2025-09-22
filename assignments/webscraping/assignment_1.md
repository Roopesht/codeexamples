# Assignment: Amazon Price Tracker

**Objective:**  
Create a Python program to monitor Amazon product prices and alert the user when a price drops below a specified benchmark.

---

**Instructions:**  

1. **Input Data**:  
   - Use a JSON file named `data.json` containing multiple records.  
   - Each record has:
     - `"url"` → Amazon product URL.  
     - `"benchmark_price"` → price threshold for alert.  

2. **Web Scraping**:  
   - Use **Selenium** to open the Amazon URLs.  
   - Use **BeautifulSoup** to parse the HTML content.  
   - Hint: Use 
     ```python
     soup.select_one("#corePriceDisplay_desktop_feature_div")
     ```
     to get the price section, and then 
     ```python
     span.a-price-whole
     ``` 
     to get the current price.

3. **Program Structure**:  
   - Create functions for:
     - Loading items from JSON.  
     - Setting up Selenium WebDriver.  
     - Fetching the latest price for a given URL.  
     - Comparing price with benchmark and triggering an alert.  
     - Playing an alert sound (e.g., opening a YouTube video).  

4. **Alert Logic**:  
   - If the current price is below the `benchmark_price`, notify the user.  
   - Hint: You can play a sound or open a YouTube video as an alert.

5. **Execution**:  
   - The program should check all items **every 1 hour** in a loop.  
   - Ensure proper error handling if a price or page cannot be fetched.  

6. **Expected Output**:  
   - For each product:
     ```
     [<URL>] Current Price: <price>, Benchmark: <benchmark_price>
     ```
   - If below benchmark:
     ```
     ✅ Price drop detected! (<price> < <benchmark_price>)
     ```

---

**Notes/Hints for Students:**  
- Use `time.sleep(3600)` to wait 1 hour between checks.  
- Use `WebDriverWait` to ensure the price element loads before scraping.  
- Organize your program using functions for readability and maintainability.
