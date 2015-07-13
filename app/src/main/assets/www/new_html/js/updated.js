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

function clearList(divName) {
    $(divName).empty();
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
        return '<img src="http://content-poc.cambridgelms.org/main/p/sites/all/themes/' +
                    'clmstouch2/img/lms-class.jpg">';
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
    if(count > 0)
        window.JSInterface.showLoadingScreen(true);
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
                                                '<div class="collection z-depth-1">' +
                                                addClass(courses[i].ClassSize, courses[i].Classes, courses[i].CourseId) +
                                                '</div>' +
                                            '</div>' +
                                        '</div>' +
                                    '</div>' +
                                '</li>').collapsible();
    //                            '<a href="content.html" class="collection-item">Class 2<span class="new badge">4</span></a>'
    }
    if(count > 0)
        window.JSInterface.showLoadingScreen(false);
}

function addUnit(count, units, courseId, classId) {
    if(count > 0)
        window.JSInterface.showLoadingScreen(true);
    for(var i = 0; i < count; i++) {
        $('#unit-list').append('<li>' +
                '<div class="collapsible-header content">' +
                    '<i class="material-icons">folder</i>' + units[i].Name +' (Unit)' +
                    '<span class="badge">'+
                        '<i class="classProgress">'+ units[i].UnitProgress+'</i>' +
                    '</span>' +
                '</div>' +
                addLesson(units[i].LessonSize, units[i].Lessons, courseId, classId, units[i].UnitId) +
            '</li>'
        ).collapsible();
        addLessonCollapsible(units[i].LessonSize, units[i].Lessons, courseId, classId, units[i].UnitId);
    }
    if(count > 0)
        window.JSInterface.showLoadingScreen(false);
}

function addLesson(count, lessons, courseId, classId, unitId) {
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
        if(lessons[i].LessonId != 0) {
            $('#'+lessons[i].LessonUniqueId).append('<li>' +
                '<div class="collapsible-header"><i class="material-icons">assignment</i>' +
                    lessons[i].LessonName +
                    '<span class="badge">'+
                        '<i class="classProgress">'+ lessons[i].LessonProgress+'</i>' +
                    '</span>' +
                '</div>' +
                addContent(lessons[i].ContentSize, lessons[i].Contents, courseId, classId,
                    unitId, lessons[i].LessonId, false) +
            '</li>'
            ).collapsible();
        }

        else {
            $('#'+lessons[i].LessonUniqueId).append('<li>' +
                addContent(lessons[i].ContentSize, lessons[i].Contents, courseId, classId,
                    unitId, lessons[i].LessonId, true) +
            '</li>').collapsible();
        }
    }

}

function addContent(count, contents, courseId, classId, unitId, lessonId, isRemoved) {
    var name = '';
    if(isRemoved)
        name += '<div class="collection">';
    else
        name += '<div class="collapsible-body">' +
                    '<div class="collection">';
    for(var i = 0; i < count; i++) {
        name += '<div class="card">' +
                    '<div class="card-content collection-item activator">' +
                        '<i class="material-icons">description</i>' + contents[i].ContentName +
                        '<i class="classProgress">' + contents[i].ContentProgress+ '</i>' +
                        '<span class="badge">' +
                            '<i class="material-icons">&#xE5CC;</i>' +
                        '</span>' +
                    '</div>' +
                    addContentIcons(contents[i].ContentDownloaded, courseId, classId
                                                    ,unitId, lessonId, contents[i].ContentId,
                                                    contents[i].ContentUniqueId);
    }
    if(isRemoved)
        name+='</div>';
    else
        name += '</div>' + '</div>';
    return name;
}

function addContentIcons(downloaded, courseId, classId, unitId, lessonId, contentId, contentUniqueId) {
    var name = '';
    name += '<div class="card-reveal" id="'+contentUniqueId+ '">' +
                '<span class="card-title grey-text text-darken-4">' +
                    '<i class="material-icons">&#xE5CD;</i>' +
                '</span>';
    name += updateContentIcon(downloaded, courseId, classId, unitId, lessonId, contentId);
    name += '</div> </div>';
    return name;
}

function updateContentIcon(downloaded, courseId, classId, unitId, lessonId, contentId) {
    var name = '';
    if(downloaded) {
            name += '<a onclick="deleteContent('+courseId+' ,'+classId+' ,'+
                                                         unitId+' ,'+lessonId+' ,'+contentId +')">' +
                        '<i class="material-icons">delete</i>' +
                        'Delete Content</a>' +
                        '<a onclick="downloadContent('+courseId+' ,'+classId+' ,'+
                            unitId+' ,'+lessonId+' ,'+contentId +')">' +
                        '<i class="material-icons">&#xE417</i>' +
                        'View Content</a>';
        }
        else {
            name += '<a onclick="downloadContent('+courseId+' ,'+classId+' ,'+
                        unitId+' ,'+lessonId+' ,'+contentId +')">' +
                        '<i class="material-icons">&#xE2C4</i>' +
                                    'Download Content</a>';
        }
    return name;
}

function refreshContents(downloaded, courseId, classId, unitId, lessonId, contentId, contentUniqueId) {
    clearList('#'+contentUniqueId);
    $('#'+contentUniqueId).append(
        '<span class="card-title grey-text text-darken-4">' +
            '<i class="material-icons">&#xE5CD;</i>' +
        '</span>' +
        updateContentIcon(downloaded, courseId, classId, unitId, lessonId, contentId));
}

function downloadContent(courseId, classId, unitId, lessonId, contentId) {
    window.JSInterface.downloadContent(courseId, classId, unitId, lessonId, contentId);
}

function deleteContent(courseId, classId, unitId, lessonId, contentId) {
    window.JSInterface.deleteContent(courseId, classId, unitId, lessonId, contentId);
}

function addLearningCourse(count, courses) {
    clearList('#course-list');
    addCourse(count, courses);
}

function addTeachingCourse(count, courses) {
    clearList('#course-list');
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