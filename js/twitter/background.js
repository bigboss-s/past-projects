chrome.runtime.onMessage.addListener(
    function (request, sender, sendResponse) {
        console.log(sender.tab ?
            "from a content script:" + sender.tab.url :
            "from the extension");
        if (request.message === "refresh")
            try {
                chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
                    chrome.tabs.reload(tabs[0].id);
                });
                sendResponse({ message: "OK", status: "200", data: null });
                return true;
            } catch (error) {
                sendResponse({ message: error.message, status: "500", data: null });
                return false;
            }
    }
);