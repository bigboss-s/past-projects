function updateStatusImage() {
    chrome.storage.local.get(['showTextTweets'], (result) => {
        const showTextTweets = result.showTextTweets !== undefined ? result.showTextTweets : false;
        const statusImage = document.getElementById('statusImage');
        statusImage.src = showTextTweets ? 'images/status_off.png' : 'images/status_on.png';
        statusImage.alt = showTextTweets ? 'filtering OFF' : 'filtering ON';
    });
}

updateStatusImage();

document.getElementById('toggle').addEventListener('click', () => {
    chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
        chrome.scripting.executeScript({
            target: { tabId: tabs[0].id },
            function: toggleFilter
        }, () => {
            updateStatusImage();
            window.close();
        });
    });
});

function toggleFilter() {
    chrome.storage.local.get(['showTextTweets'], (result) => {
        const currentState = result.showTextTweets !== undefined ? result.showTextTweets : true;
        const newState = !currentState;

        chrome.storage.local.set({ showTextTweets: newState }, () => {
            chrome.runtime.sendMessage({ message: "refresh" }, function (response) {
                console.log(response.message);
            });
        });
    });
}
