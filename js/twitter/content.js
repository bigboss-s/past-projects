function filterTweets(showTextTweets) {
    const tweets = document.querySelectorAll('article');

    if (!showTextTweets) {
        tweets.forEach(tweet => {
            const hasMedia = tweet.querySelector('div[data-testid="tweetPhoto"]');

            if (hasMedia) {
                console.log("has media");
                tweet.style.display = 'block';
            } else {
                console.log("no media");
                tweet.style.display = 'none';
            }
        });
    }
}

function loadShowTextTweets() {
    chrome.storage.local.get(['showTextTweets'], (result) => {
        const showTextTweets = result.showTextTweets !== undefined ? result.showTextTweets : true;
        filterTweets(showTextTweets);
    });
}

const observer = new MutationObserver(loadShowTextTweets);
observer.observe(document.body, { childList: true, subtree: true });

loadShowTextTweets();
