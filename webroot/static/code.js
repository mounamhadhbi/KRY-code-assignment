const listContainer = document.querySelector('#service-list')
const info = document.querySelector('#info');
let servicesRequest = new Request('/service');


function refreshServiceList() {
    fetch(servicesRequest)
        .then(function (response) {
            return response.json();
        })
        .then(function (serviceList) {
            var child = listContainer.lastElementChild;
            while (child) {
                listContainer.removeChild(child);
                child = listContainer.lastElementChild;
            }
            serviceList.forEach(service => {
                let li = document.createElement("li");
                let color = 'blue';
                if (service.status == 'OK') {
                    color = 'green';
                } else if (service.status == 'FAILED') {
                    color = 'red';
                }
                li.style.color = color;
                li.appendChild(document.createTextNode(service.name + ': ' + service.url + ' (' + service.status + ')'));
                listContainer.appendChild(li);
            });
        });
}

refreshServiceList();
let intervalID = setInterval(function () {
    refreshServiceList();
}, 5000);

const saveButton = document.querySelector('#post-service');
saveButton.onclick = evt => {
    let urlName = document.querySelector('#url-name').value;
    document.querySelector('#url-name').value = '';
    let serviceName = document.querySelector('#service-name').value;
    document.querySelector('#service-name').value = '';
    fetch('/service', {
        method: 'post',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({url: urlName, name: serviceName})
    })
        .then(res => res.json())
        .then(data => {
            console.log(data);
            info.appendChild(document.createTextNode(data.message));
            let div = document.getElementById('divInfo');
            div.style.display = "block";
        })
        .then(nothing => {
            setTimeout(() => {
                info.removeChild(info.firstChild);
                let div = document.getElementById('divInfo');
                div.style.display = "none";
            }, 3000);
        });
};

const deleteButton = document.querySelector('#delete-service');
deleteButton.onclick = evt => {
    let urlName = document.querySelector('#url-name').value;
    document.querySelector('#url-name').value = '';
    fetch('/service', {
        method: 'delete',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({url: urlName})
    })
        .then(res => res.json())
        .then(data => {
            console.log(data);
            let div = document.getElementById('divInfo');
            div.style.display = "block";
            info.appendChild(document.createTextNode(data.message));
        })
        .then(nothing => {
            setTimeout(() => {
                info.removeChild(info.firstChild);
                let div = document.getElementById('divInfo');
                div.style.display = "none";
            }, 3000);
        });
};