let imagePicker = document.getElementById("imagePicker");
let previewImg = document.getElementById("previewImg");
let file;

imagePicker.addEventListener("change", function () {
    event.preventDefault();
    previewImg.style.display = "block";
    previewImage();
});

function previewImage() {
    file = document.querySelector('input[type=file]').files[0];
    let reader = new FileReader();

    reader.onloadend = function () {
        previewImg.src = reader.result;
    };

    if (file) {
        reader.readAsDataURL(file);
    } else {
        alert('We couldn\'t load the file');
        previewImg.style.display = "none";
        previewImg.src = "";
    }
}

function sendImage() {
    if (file) {
        let form = new FormData();
        form.append("imageFile", file);
        let settings = {
            "async": true,
            "crossDomain": true,
            "url": "http://localhost:8080/v1/loadImage",
            "method": "POST",
            "headers": {
                "Cache-Control": "no-cache"
            },
            "processData": false,
            "contentType": false,
            "mimeType": "multipart/form-data",
            "data": form
        };

        $.ajax(settings).done(function (response) {
            let answer = JSON.parse(response);
            if (answer.success === false) {
                alert('Error loading image.')
            } else {
                alert('image loaded.')
            }
        });
    } else {
        alert('Please select an image.')
    }
}