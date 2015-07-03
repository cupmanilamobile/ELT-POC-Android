function showVideo() {
    window.JSInterface.showVideo();
}

function hideVideo() {
    window.JSInterface.hideVideo();
}

function signOutUser() {
    window.JSInterface.signOutUser();
}

function updateLink(message) {
    window.location.assign(message);
}

function clearList() {
    $('#course-list').empty();
}

function updateClassName(className) {
    window.JSInterface.updateClassName(className);
}

function addClass(count, classes) {
    var name = '';
    for(var i = 0; i < count; i++) {
         name += '<a href="content.html" class="collection-item" onclick="updateClassName('+
         "'"+classes[i].ClassName + "'"+')">' +
         classes[i].ClassName + '</a>';
    }
    return name;
}

function addImage(image) {
    if(image == '')
        return '<img src="images/cambridge-touchstone.jpg">';
    else
        return '<img src="'+ image +'">';
}

function truncateName(name) {
    if(name.length > 30)
        return name.substring(0, 27) + '...';
    else
        return name;
}

function addCourse(count, courses) {
    for(var i = 0; i < count; i++) {
            $('#course-list').append('<li>' +
                                '<div class="collapsible-header class">' +
                                    '<i class="material-icons">class</i>' + truncateName(courses[i].Name) +
                                '</div>' +
                                    '<div class="collapsible-body class">' +
                                        '<div class="row">' +
                                            '<div class="col s12 m4 l3">' +
                                                 addImage(courses[i].Image)+
                                            '</div>' +
                                            '<div class="col s12 m8 l9">' +
                                                '<h2>'+ courses[i].Name +'</h2>' +
                                                '<div class="author">Dr. Julius Wilson</div>' +
                                                '<p>Uniquely incubate one-to-one manufactured products through 24/365 niches. Monotonectally unleash. </p>' +
                                                '<div class="collection">' +
                                                addClass(courses[i].ClassSize, courses[i].Classes) +
                                                '</div>' +
                                            '</div>' +
                                        '</div>' +
                                    '</div>' +
                                '</li>').collapsible();
    //                            '<a href="content.html" class="collection-item">Class 2<span class="new badge">4</span></a>'
        }
}

function addLearningCourse(count, courses) {
    clearList();
    addCourse(count, courses);
}

function addTeachingCourse(count, courses) {
    clearList();
    addCourse(count, courses);
}

function scrollToElement(id) {
    var elem = document.getElementById(id);
    var x = 0;
    var y = 0;

    while (elem != null) {
        x += elem.offsetLeft;
        y += elem.offsetTop;
        elem = elem.offsetParent;
    }
    window.scrollTo(x, y);
}