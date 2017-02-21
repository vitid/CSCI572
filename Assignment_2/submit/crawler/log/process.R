library(dplyr)

setwd("/home/vitidn/mydata/repo_git/CSCI572/Assignment_2/submit/crawler/log/")

fetch_data = read.csv("fetch_NY_Times.csv")
table(fetch_data$httpStatusCode)

#TA's answer:
# * All 300's could be considered as aborted
# * 404 could be considered failed
# * Now 503 is service un available, which is a failure and not aborted. 
#Not sure how to categorize Fail/Abort?
#  200   301   302   303   404   503 
#14904  4855   105    98    32     6

url_data = read.csv("urls_NY_Times.csv")
length(unique(url_data$url))
url_data %>% filter(isOk == "OK") %>% distinct(url) %>% summarise(count=n())
url_data %>% filter(isOk == "N_OK") %>% distinct(url) %>% summarise(count=n())

visit_data = read.csv("visit_NY_Times.csv")
table(visit_data$content.type)

categorizeFileSize <- function(byteSize){
    if(byteSize < 1 * 1024){
        return("0")
    }else if(byteSize < 10 * 1024){
        return("1")
    }else if(byteSize < 100 * 1024){
        return("2")
    }else if(byteSize < 1024 * 1024){
        return("3")
    }
    return("4")
}
visit_data$fileSizeCategory = sapply(visit_data$size,categorizeFileSize)
table(visit_data$fileSizeCategory)
