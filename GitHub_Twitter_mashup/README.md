# Github and twitter mashup
Command line API mashup of the GitHub and Twitter APIs. Search for "*" projects on GitHub, then for each project search for tweets that mention it. It output a summary of each project with a short list of recent tweets, in json format.


## Usage
    You have to put your Twitter App credentials in `.../resources/application.conf` and tweet's option.
    Note : Rate limits are divided into 15 minute intervals and 180 calls every 15 minutes.

Running:

```sbt run```
