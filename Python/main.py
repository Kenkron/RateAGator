from bs4 import BeautifulSoup as BS
import urllib2

FALL_2012 = "http://www.registrar.ufl.edu/soc/201208/all/"
AMAZON_BASE = "http://www.amazon.com/s/ref=nb_sb_noss/176-8710242-7445831?url=search-alias%3Daps&field-keywords="
OFFSET = 24;

# course listing links
def getListingLinks(baseURL):
    print "Getting listing links"
    soup = BS(urllib2.urlopen(baseURL).read())
    listing_links = [str(x['value']) for x in soup.find(id='soc_content').find_all('option')]
    del listing_links[0]
    print "listing links " + str(len(listing_links))
    return listing_links

# get all the textbook links from course page (has duplicates)
def getCoursePageLinks(listing_links, baseURL):
    print "Getting course page links"
    course_page_links = list()
    for i in range(len(listing_links) - OFFSET):
        soup = BS(urllib2.urlopen(baseURL + listing_links[i+OFFSET]).read())
        for x in soup.find(id="soc_content").find_all('a'):
            if x.has_key("href"):
                if "books" in x["href"]:
                    course_page_links.append(x["href"])
    return course_page_links
def getIsbns(book_page_links):
    print "Getting ISBNs"
    previousCode= ""
    previousProf = ""
    bookISBNs = list()
    for link in book_page_links:
        soup = BS(urllib2.urlopen(link).read())
        try:
            currentCode = soup.find("td", "h2 course").text.strip()
            currentProf = soup.find("td", "h2 instructor").text.strip()
        except AttributeError:
            continue
            
        #checking if should write to file or not
        if currentProf != previousProf:
            if len(bookISBNs) != 0:
                #write to file
                with open("log1_3.txt", "a") as myfile:
                    myfile.write(previousCode.encode('utf-8') + " " + previousProf.encode('utf-8'))
                    for member in bookISBNs:
                        myfile.write(" " + member.encode('utf-8'))
                    myfile.write("\n")
            print previousCode + " is complete!"
            previousCode = currentCode
            previousProf = currentProf
            bookISBNs = list()
        
        try:
            #looks for isbns
            bookList = soup.find("table", "books").findNext("tbody").findNext("tr").findAllNext("td")
            for a in bookList:
                if a.text.strip() == "ISBN:":
                    currentISBN = a.findNext("td").text.strip()
                    if currentISBN == "":
                        continue
                    try:
                        #attempt to get amazon url
                        amazon_search_url = AMAZON_BASE + currentISBN
                        soup = BS(urllib2.urlopen(amazon_search_url).read())
                        book_page_url = soup.find_all("div", id="result_0")[0].findNext("a")["href"]
                        if book_page_url in bookISBNs:
                            continue
                        else:
                            bookISBNs.append(book_page_url)
                    except IndexError:
                        #if not, just submit the isbn number
                        continue
        except AttributeError:
                continue
    
listing_links = getListingLinks(FALL_2012)
course_page_links = getCoursePageLinks(listing_links, FALL_2012)
getIsbns(course_page_links)
