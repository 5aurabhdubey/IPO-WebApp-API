<<<<<<< HEAD
import requests
import logging
from django.conf import settings
from datetime import datetime
import time

# Configure logging
logger = logging.getLogger(__name__)

class InvalidIPODataException(Exception):
    """Custom exception for invalid IPO data."""
    pass

def validate_ipo_data(ipo):
    """Validate basic IPO stock data from search API."""
    required_fields = ["symbol", "name", "stockExchange", "currency"]

    for field in required_fields:
        if not ipo.get(field) or ipo[field] in ["N/A", None, ""]:
            logger.warning(f"Skipping IPO: Missing or invalid field {field}")
            return False

    return True

def validate_ipo_details(details):
    """Validate detailed IPO data from company profile API."""
    try:
        # Validate listing date
        if details.get("ipoDate") and details["ipoDate"] not in ["N/A", None, ""]:
            datetime.strptime(details["ipoDate"], "%Y-%m-%d")  # Check valid format

        # Validate numeric fields
        for field in ["ipoPrice", "price", "listingPrice"]:
            if details.get(field) and details[field] not in ["N/A", None, ""]:
                float(details[field])  # Convert to float to ensure valid number

        return True
    except (ValueError, TypeError) as e:
        logger.error(f"Invalid IPO details format: {e}")
        return False
    
def safe_get(data, key, default="N/A"):
    """Safely get a value from a dictionary, returning a default if key is missing."""
    return data.get(key, default)

def safe_float(value):
    """Convert value to float safely, returning None if conversion fails."""
    try:
        return float(value) if value not in ["N/A", None, ""] else None
    except ValueError:
        return None


def fetch_ipo_calendar():
    """Fetch, validate, and return IPO data with error handling."""
    api_key = settings.API_KEYS.get("FMP")
    if not api_key:
        logger.error("API Key missing for FMP")
        return {"error": "API Key missing for FMP"}

    base_url = "https://financialmodelingprep.com/api/v3/ipo_calendar"
    detailed_ipo_list = []

    try:
        response = requests.get(f"{base_url}?apikey={api_key}")
        
        # Handle unauthorized access
        if response.status_code == 403:
            logger.error("403 Forbidden - API key might be invalid or restricted. Check your FMP subscription.")
            return {"error": "API key is not authorized to access this data."}
        
        response.raise_for_status()
        ipo_data = response.json()

        if not ipo_data:
            logger.info("No IPO stocks found")
            return {"error": "No IPO stocks found"}

        for stock in ipo_data:
            symbol = safe_get(stock, "symbol")
            company_name = safe_get(stock, "name")
            stock_exchange = safe_get(stock, "exchange")
            ipo_price = safe_float(safe_get(stock, "price"))
            listing_date = safe_get(stock, "date")

            # Append IPO data
            detailed_ipo_list.append({
                "symbol": symbol,
                "company_name": company_name,
                "stock_exchange": stock_exchange,
                "ipo_price": ipo_price,
                "listing_date": listing_date,
            })

            # ✅ Smart rate-limiting: Delay only every 10 requests
            if len(detailed_ipo_list) % 10 == 0:
                time.sleep(1)

        return {"ipo_calendar": detailed_ipo_list}

    except requests.RequestException as e:
        logger.error(f"Failed to fetch IPO stocks: {e}")
        return {"error": f"Failed to fetch IPO stocks: {str(e)}"}


def fetch_stock_price(symbol):
    """Fetch real-time stock price from Twelve Data API with error handling."""
    api_key = settings.API_KEYS.get("TWELVE_DATA")
    if not api_key:
        logger.error("API Key missing for TWELVE_DATA")
        return {"error": "API Key missing for TWELVE_DATA"}

    url = f"https://api.twelvedata.com/price?symbol={symbol}&apikey={api_key}"

    try:
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()
        return data.get("price", {"error": "Stock price not found"})  # Ensure valid response
    except requests.RequestException as e:
        logger.error(f"Failed to fetch stock price for {symbol}: {e}")
        return {"error": f"Failed to fetch stock price: {str(e)}"}

def fetch_company_logo(company_name):
    """Fetch company logo (or icon) from Brandfetch API with error handling."""
    api_key = settings.API_KEYS.get("BRANDFETCH")
    if not api_key:
        logger.error("API Key missing for BRANDFETCH")
        return {"error": "API Key missing for BRANDFETCH"}

    url = f"https://api.brandfetch.io/v2/search/{company_name}?c={api_key}"
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Accept": "application/json"
    }

    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        data = response.json()

        # Ensure response contains at least one brand
        if isinstance(data, list) and len(data) > 0:
            first_company = data[0]
            if "icon" in first_company:
                return {"company": company_name, "logo_url": first_company["icon"]}

        return {"error": f"No logo or icon found for {company_name}. API response: {data}"}

    except requests.RequestException as e:
        logger.error(f"Brandfetch API error: {e}")
        return {"error": f"Brandfetch API error: {str(e)}"}
=======
import requests
import logging
from django.conf import settings
from datetime import datetime
import time

# Configure logging
logger = logging.getLogger(__name__)

class InvalidIPODataException(Exception):
    """Custom exception for invalid IPO data."""
    pass

def validate_ipo_data(ipo):
    """Validate basic IPO stock data from search API."""
    required_fields = ["symbol", "name", "stockExchange", "currency"]

    for field in required_fields:
        if not ipo.get(field) or ipo[field] in ["N/A", None, ""]:
            logger.warning(f"Skipping IPO: Missing or invalid field {field}")
            return False

    return True

def validate_ipo_details(details):
    """Validate detailed IPO data from company profile API."""
    try:
        # Validate listing date
        if details.get("ipoDate") and details["ipoDate"] not in ["N/A", None, ""]:
            datetime.strptime(details["ipoDate"], "%Y-%m-%d")  # Check valid format

        # Validate numeric fields
        for field in ["ipoPrice", "price", "listingPrice"]:
            if details.get(field) and details[field] not in ["N/A", None, ""]:
                float(details[field])  # Convert to float to ensure valid number

        return True
    except (ValueError, TypeError) as e:
        logger.error(f"Invalid IPO details format: {e}")
        return False
    
def safe_get(data, key, default="N/A"):
    """Safely get a value from a dictionary, returning a default if key is missing."""
    return data.get(key, default)

def safe_float(value):
    """Convert value to float safely, returning None if conversion fails."""
    try:
        return float(value) if value not in ["N/A", None, ""] else None
    except ValueError:
        return None


def fetch_ipo_calendar():
    """Fetch, validate, and return IPO data with error handling."""
    api_key = settings.API_KEYS.get("PLG")
    if not api_key:
        logger.error("API Key missing for PLG")
        return {"error": "API Key missing for FMP"}
    data = []
    detailed_ipo_list = []
    base_url = f"https://api.polygon.io/vX/reference/ipos?order=desc&limit=10&sort=listing_date&apikey={api_key}"
    try:
        while base_url:
            response = requests.get(base_url)
            if response.status_code == 403:
                logger.error("403 Forbidden - API key might be invalid or restricted. Check your FMP subscription.")
                return {"error": "API key is not authorized to access this data."}
            response_json = response.json()
            results = response_json.get('results',[])
            data.extend(results)
            next_url = response_json.get('next_url')
            if next_url:
                base_url = f"{next_url}&apikey={api_key}"
            else:
                base_url = None
        for stock in data:
            symbol = safe_get(stock, "ticker")
            company_name = safe_get(stock, "issuer_name")
            stock_exchange = safe_get(stock, "primary_exchange")
            ipo_price_lowest = safe_float(safe_get(stock, "lowest_offer_price"))
            ipo_price_highest = safe_float(safe_get(stock, "highest_offer_price"))
            listing_date = safe_get(stock,"listing_date")
            opening_date = safe_get(stock,"announced_date")
            close_date = safe_get(stock,"issue_end_date")
            issue_size = safe_get(stock,"total_offer_size")
            status = safe_get(stock,"ipo_status")
            currency = safe_get(stock,"currency_code")
            detailed_ipo_list.append({
                "symbol": symbol,
                "company_name": company_name,
                "stock_exchange": stock_exchange,
                "ipo_price": f"{ipo_price_lowest}-{ipo_price_highest}",
                "listing_date": listing_date ,
                "opening_date":opening_date,
                "close_date":close_date,
                "issue_size":issue_size,
                "status":status,
                "currency":currency,

            })
        if len(detailed_ipo_list) % 10 == 0:
            time.sleep(1)

        return {"ipo_calendar": detailed_ipo_list}
    except requests.RequestException as e:
        logger.error(f"Failed to fetch IPO stocks: {e}")

        
def fetch_stock_price(symbol):
    """Fetch real-time stock price from Twelve Data API with error handling."""
    api_key = settings.API_KEYS.get("TWELVE_DATA")
    if not api_key:
        logger.error("API Key missing for TWELVE_DATA")
        return {"error": "API Key missing for TWELVE_DATA"}

    url = f"https://api.twelvedata.com/price?symbol={symbol}&apikey={api_key}"

    try:
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()
        return data.get("price", {"error": "Stock price not found"})  # Ensure valid response
    except requests.RequestException as e:
        logger.error(f"Failed to fetch stock price for {symbol}: {e}")
        return {"error": f"Failed to fetch stock price: {str(e)}"}

def fetch_company_logo(company_name):
    """Fetch company logo (or icon) from Brandfetch API with error handling."""
    api_key = settings.API_KEYS.get("BRANDFETCH")
    if not api_key:
        logger.error("API Key missing for BRANDFETCH")
        return {"error": "API Key missing for BRANDFETCH"}

    url = f"https://api.brandfetch.io/v2/search/{company_name}?c={api_key}"
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Accept": "application/json"
    }

    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        data = response.json()

        # Ensure response contains at least one brand
        if isinstance(data, list) and len(data) > 0:
            first_company = data[0]
            if "icon" in first_company:
                return {"company": company_name, "logo_url": first_company["icon"]}

        return {"error": f"No logo or icon found for {company_name}. API response: {data}"}

    except requests.RequestException as e:
        logger.error(f"Brandfetch API error: {e}")
        return {"error": f"Brandfetch API error: {str(e)}"}
>>>>>>> bcb07b7f77cb96eb6a526fce88b049b5bce7bd92
