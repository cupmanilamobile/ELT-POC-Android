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

function updateClassName(className, courseId, classId) {
    if(window.JSInterface.hasInternetConnection())
        window.location.replace("content.html");
    else
        window.location.replace("content_downloaded.html")
    window.JSInterface.updateClassName(className, courseId, classId);
}

function addClass(count, classes, courseId) {
    var name = '';
    for(var i = 0; i < count; i++) {
         name += '<a class="collection-item" onclick="updateClassName('+
         "'"+classes[i].ClassName + "' ,"+ courseId +' ,'+classes[i].ClassId +')">' +
         classes[i].ClassName + '</a>';
    }
    return name;
}

function addImage(image) {
    if(image == '')
        return '<img src="images/no-image.png">';
    else
        return '<img src="'+ image +'">';
}

function truncateName(name) {
    if(window.JSInterface.isPhone() && name.length > 30)
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
                                                '<span class="class label"></span>' +
                                                '<p>Uniquely incubate one-to-one manufactured products through 24/365 niches. Monotonectally unleash. </p>' +
                                                '<div class="collection z-depth-1">' +
                                                addClass(courses[i].ClassSize, courses[i].Classes, courses[i].CourseId) +
                                                '</div>' +
                                            '</div>' +
                                        '</div>' +
                                    '</div>' +
                                '</li>').collapsible();
    //                            '<a href="content.html" class="collection-item">Class 2<span class="new badge">4</span></a>'
        }
}

function addUnit(count, units, courseId, classId) {
    for(var i = 0; i < count; i++) {
        $('#unit-list').append('<li>' +
                '<div class="collapsible-header content">' +
                    '<i class="material-icons">folder</i>' + units[i].Name +' (Unit)' +
                '</div>' +
                addLesson(units[i].LessonSize, units[i].Lessons) +
            '</li>'
        ).collapsible();
        addLessonCollapsible(units[i].LessonSize, units[i].Lessons, courseId, classId, units[i].UnitId);
    }
}

function addLesson(count, lessons) {
    var name = '';
        for(var i = 0; i < count; i++) {
             name += '<div class="collapsible-body content">' +
                        '<ul class="collapsible" data-collapsible="accordion" id="'+lessons[i].LessonUniqueId+ '">' +
                        '</ul>' +
                      '</div>';
        }
        return name;
}

function addLessonCollapsible(count, lessons, courseId, classId, unitId) {
    for(var i = 0; i < count; i++) {
        $('#'+lessons[i].LessonUniqueId).append('<li>' +
            '<div class="collapsible-header"><i class="material-icons">assignment</i>' +
                lessons[i].LessonName +
            '</div>' +
            addContent(lessons[i].ContentSize, lessons[i].Contents, courseId, classId, unitId, lessons[i].LessonId) +
            '</li>'
        ).collapsible();
    }

}

function addContent(count, contents, courseId, classId, unitId, lessonId) {
    var name = '';
    name += '<div class="collapsible-body">' +
                '<div class="collection">';
    for(var i = 0; i < count; i++) {
        name += '<a class="collection-item" onclick="downloadContent('+courseId+' ,'+classId+' ,'+
                    unitId+' ,'+lessonId+' ,'+contents[i].ContentId +')">' +
                    '<i class="material-icons">description</i>' +
                        contents[i].ContentName +
                        '<span class="badge">' +
                            addContentIcons(contents[i].ContentDownloaded) +
                        '</span>' +
                '</a>';
    }
    name += '</div>' + '</div>';
    return name;
}

function addContentIcons(downloaded) {
    var name = '';
    if(downloaded) {
        name += '<i class="material-icons">delete</i>' +
             '<i class="material-icons">&#xE5D4</i>';
    }
    else {
        name += '<i class="material-icons">&#xE2C4</i>' +
        '<i class="material-icons">&#xE5D4</i>';
    }
    return name;
}

function downloadContent(courseId, classId, unitId, lessonId, contentId) {
    window.JSInterface.downloadContent(courseId, classId, unitId, lessonId, contentId);
}

//    <div class="collapsible-body content">
//
//
//                            <ul class="collapsible" data-collapsible="accordion">
//                                <li>
//                                    <div class="collapsible-header"><i class="material-icons">assignment</i>First
//                                        Lesson
//                                    </div>
//                                    <div class="collapsible-body">
//                                        <div class="collection">
//                                            <a href="video.html" class="collection-item"><i
//                                                    class="material-icons">description</i> Content 1
//                  <span class="badge">
//                    <i class="material-icons">file_download</i>
//                    <i class="material-icons">delete</i>
//                    <i class="material-icons">more_vert</i>
//                  </span>
//                                            </a>
//                                            <a href="video.html" class="collection-item"><i
//                                                    class="material-icons">description</i> Content 2
//                <span class="badge">
//                    <i class="material-icons">file_download</i>
//                    <i class="material-icons">delete</i>
//                    <i class="material-icons">more_vert</i>
//                  </span>
//                                            </a>
//                                            <a href="video.html" class="collection-item"><i
//                                                    class="material-icons">description</i> Content 3
//                  <span class="badge">
//                    <i class="material-icons">file_download</i>
//                    <i class="material-icons">delete</i>
//                    <i class="material-icons">more_vert</i>
//                  </span>
//                                            </a>
//                                            <a href="video.html" class="collection-item"><i
//                                                    class="material-icons">description</i> Content 4
//                  <span class="badge">
//                    <i class="material-icons">file_download</i>
//                    <i class="material-icons">delete</i>
//                    <i class="material-icons">more_vert</i>
//                  </span>
//                                            </a>
//                                        </div>
//                                    </div>
//                                </li>
//                                <li>
//                                    <div class="collapsible-header"><i class="material-icons">assignment</i>Second
//                                        Lesson
//                                    </div>
//                                    <div class="collapsible-body">
//                                        <div class="collection">
//                                            <a href="video.html" class="collection-item"><i
//                                                    class="material-icons">description</i> Content 1
//                  <span class="badge">
//                    <i class="material-icons">file_download</i>
//                    <i class="material-icons">delete</i>
//                    <i class="material-icons">more_vert</i>
//                  </span>
//                                            </a>
//                                            <a href="video.html" class="collection-item"><i
//                                                    class="material-icons">description</i> Content 2
//                  <span class="badge">
//                    <i class="material-icons">file_download</i>
//                    <i class="material-icons">delete</i>
//                    <i class="material-icons">more_vert</i>
//                  </span>
//                                            </a>
//                                        </div>
//                                    </div>
//                                </li>
//                                <li>
//                                    <div class="collapsible-header"><i class="material-icons">assignment</i>Third
//                                        Lesson
//                                    </div>
//                                    <div class="collapsible-body">
//                                        <div class="collection">
//                                            <a href="video.html" class="collection-item"><i
//                                                    class="material-icons">description</i> Content 1
//                  <span class="badge">
//                    <i class="material-icons">file_download</i>
//                    <i class="material-icons">delete</i>
//                    <i class="material-icons">more_vert</i>
//                  </span>
//                                            </a>
//                                            <a href="video.html" class="collection-item"><i
//                                                    class="material-icons">description</i> Content 2
//                  <span class="badge">
//                    <i class="material-icons">file_download</i>
//                    <i class="material-icons">delete</i>
//                    <i class="material-icons">more_vert</i>
//                  </span>
//                                            </a>
//                                        </div>
//                                    </div>
//                                </li>
//                            </ul>
//                        </div>
//                    </li>

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