from bs4 import BeautifulSoup as BS
import urllib2

FALL_2012 = "http://www.registrar.ufl.edu/soc/201208/all/"
AMAZON_BASE = "http://www.amazon.com/s/ref=nb_sb_noss/176-8710242-7445831?url=search-alias%3Daps&field-keywords="

# course listing links
def getListingLinks(baseURL):
    print "Getting listing links"
    soup = BS(urllib2.urlopen(baseURL).read())
    listing_links = [str(x['value']) for x in soup.find(id='soc_content').find_all('option')]
    del listing_links[0]
    return listing_links

# get all the textbook links from course page (has duplicates)
def getCoursePageLinks(listing_links, baseURL):
    print "Getting course page links"
    course_page_links = list()
    for listing in listing_links:
        soup = BS(urllib2.urlopen(baseURL + listing).read())
        for x in soup.find(id="soc_content").find_all('a'):
            if x.has_key("href"):
                if "books" in x["href"]:
                    course_page_links.append(x["href"])
    return course_page_links
def getIsbns(book_page_links):
    print "Getting ISBNs"
    previousCode = ""
    bookISBNs = list()
    for link in book_page_links:
        soup = BS(urllib2.urlopen(link).read())
        try:
            currentCode = soup.find("td", "h2 course").text.strip()
        except AttributeError:
            continue
            
        #checking if should write to file or not
        if currentCode != previousCode:
            if len(bookISBNs) != 0:
                #write to file
                with open("log.txt", "a") as myfile:
                    myfile.write(previousCode.encode('utf-8'))
                    for member in bookISBNs:
                        myfile.write((" " + member).encode('utf-8'))
                    myfile.write("\n".encode('utf-8'))
            print previousCode + " is complete!"
            previousCode = currentCode
            bookISBNs = list()
        
        try:
            #looks for isbns
            bookList = soup.find("table", "books").findNext("tbody").findNext("tr").findAllNext("td")
            for a in bookList:
                if a.text.strip() == "ISBN:":
                    currentISBN = a.findNext("td").text.strip()
                    if currentISBN == "":
                        continue
                    if currentISBN in bookISBNs:
                        continue
                    else:
                        try:
                            #attempt to get amazon url
                            amazon_search_url = AMAZON_BASE + currentISBN
                            soup = BS(urllib2.urlopen(amazon_search_url).read())
                            book_page_url = soup.find_all("div", id="result_0")[0].findNext("a")["href"]
                            bookISBNs.append(book_page_url)
                        except IndexError:
                            #if not, just submit the isbn number
                            bookISBNs.append(currentISBN)
        except AttributeError:
                continue
    
listing_links = getListingLinks(FALL_2012)
course_page_links = getCoursePageLinks(listing_links, FALL_2012)
getIsbns(course_page_links)
