var adapterName = [ {
    'NetAgent' : 'Network'
}, {
    'dbmonitor' : 'DataBase'
}, {
    'jvmlocal' : 'JVM Local'
}, {
    'jvmremote' : 'JVM Remote'
}, {
    'gmetad' : 'Server'
} ];
var charttype;
var selectedJson = "server"; // comboboxAll()
var menuHeading = "chartHead"; // menuHover
var host = "";
var tempSelectedtab = "";
var linechartdataforip;
var hostUrl;
// var linechartdata;
var listOfMetricNames = [];
var leftpanwidth;
var rightpanwidth;
var common_ResourceMetricNames = [];
var common_ResourceMetricValues = [];
var common_ResourceMetricSlaValues = [];
var common_ResourceMetricHealthStatus = [];
var common_ResourceMetricHealthCount = [];
var commmon_ResourceAlerts = [];
var commmon_ResourceAlertValues = [];
var commmon_ResourceAlertStatusNames = [];
var commmon_ResourceAlertStatusValues = [];
var commmon_ResourceAlertAssigneeNames = [];
var commmon_ResourceAlertAssigneeValues = [];
// var window1;
// var grid1;
var selectedIP = "";
// var gangaliaDownedTimestamp = "";
// var criticalMetricType = "";
var timeout_interval;
// var live_kendo_chart;
var POINTS = 500, categoryList = [], stocks;
var seriesArray = new Array();
var first = true;
var metriclist;
var criticalMetricList = ""; // DrawLineChart
// var ss = 0;
var secoundCount = 0;
var datasourceLive;
var xAxisLabelsLive;
var yAxisValuesLive;
var MetricNameLive;
var xlablesMinutes;
var xlablesHours;
var diffTime = 0;
var timeFlag = true;
// var gridDataSource;
var chartHeading = "";
var data1;
var data2;
var data3;
var data4;
var JResourceId = "";
var pivotValue = "";
var repeatedValue = "";
// var lastvalue="";
var inc = 0;
var linetimestamps = [];
var availTimestamps = [];
var resourceName = [];
var checkAdapterProgress = true;
var realMetricType = "";
var menuItemSelected = [];
var serverDownList = [];
var selected_rowrecord;
var selectedTheme;
var displayMetrictype = "";

var watchdogstatus = [];

function listOfServerDown() {
    // var linechartdata = srReadFiles1(customerID  + "/" + $("#datepicker").val() + "/Hour/WatchDogAlert.json");
    var linechartdata = srReadFiles1(customerID  + "/" + $("#datepicker").val() + "/WatchDogAlert.json");
    for ( var c = 0; c < linechartdata.length; c++) {
        if (linechartdata[c].ResourceType == selectedtab.toLowerCase()
            || linechartdata[c].ResourceType == selectedtab)
            serverDownList.push(linechartdata[c].ServerName);
    }
}

/** For Selecting the Menu from 1st Quarter of Chart */
function onSelect1(e) {
    var item = $(e.item);
    menuElement = item.closest(".k-menu");
    dataItem = data1[0].items;
    index = item.parentsUntil(menuElement, ".k-item").map(function() {
        return $(this).index();
    }).get().reverse();

    index.push(item.index());

    for ( var i = -1, len = index.length; ++i < len;) {
        dataItem = dataItem[index[i]];
        dataItem = i < len - 1 ? dataItem.items : dataItem;
    }

    if ((dataItem.value).length == 6 && (dataItem.value).indexOf('Health') == 0) {
        return;
    } else if ((dataItem.value).length == 5
        && dataItem.value.indexOf('Alert') == 0) {
        return;
    } else {
        menuIndexes[0] = dataItem;
        menuItemSelected[0] = dataItem.text;
        listOfMetricNames[0] = dataItem.metricType;
        $("#" + chartheaddiv[0]).val(dataItem.value);

    }

    var menu1 = (dataItem.value).replace(/ /g, '');
    DrawChart(combodiv[10], menu1, (dataItem.charttype));
}

/** For Selecting the Menu from 2nd Quarter of Chart */
function onSelect2(e) {
    var item = $(e.item);
    menuElement = item.closest(".k-menu");
    dataItem = data2[0].items;
    index = item.parentsUntil(menuElement, ".k-item").map(function() {
        return $(this).index();
    }).get().reverse();

    index.push(item.index());

    for ( var i = -1, len = index.length; ++i < len;) {
        dataItem = dataItem[index[i]];
        dataItem = i < len - 1 ? dataItem.items : dataItem;
    }
    if ((dataItem.value).length == 6 && (dataItem.value).indexOf('Health') == 0) {
        return;
    } else if ((dataItem.value).length == 5
        && dataItem.value.indexOf('Alert') == 0) {
        return;
    } else {
        menuIndexes[1] = dataItem;

        menuItemSelected[1] = dataItem.text;
        listOfMetricNames[1] = dataItem.metricType;
        $("#" + chartheaddiv[1]).val(dataItem.value);

    }
    var menu1 = (dataItem.value).replace(/ /g, '');
    DrawChart(combodiv[11], menu1, (dataItem.charttype));
}

/** For Selecting the Menu from 3rd Quarter of Chart */
function onSelect3(e) {
    var item = $(e.item);
    menuElement = item.closest(".k-menu");
    dataItem = data3[0].items;
    index = item.parentsUntil(menuElement, ".k-item").map(function() {
        return $(this).index();
    }).get().reverse();

    index.push(item.index());

    for ( var i = -1, len = index.length; ++i < len;) {
        dataItem = dataItem[index[i]];
        dataItem = i < len - 1 ? dataItem.items : dataItem;
    }
    if ((dataItem.value).length == 6 && (dataItem.value).indexOf('Health') == 0) {
        return;
    } else if ((dataItem.value).length == 5
        && dataItem.value.indexOf('Alert') == 0) {
        return;
    } else {
        menuIndexes[2] = dataItem;

        menuItemSelected[2] = dataItem.text;
        listOfMetricNames[2] = dataItem.metricType;
        $("#" + chartheaddiv[2]).val(dataItem.value);

    }

    var menu1 = (dataItem.value).replace(/ /g, '');
    DrawChart(combodiv[12], menu1, (dataItem.charttype));
}

/** For Selecting the Menu from 4th Quarter of Chart */
function onSelect4(e) {
    var item = $(e.item);
    menuElement = item.closest(".k-menu");
    dataItem = data4[0].items;
    index = item.parentsUntil(menuElement, ".k-item").map(function() {
        return $(this).index();
    }).get().reverse();

    index.push(item.index());

    for ( var i = -1, len = index.length; ++i < len;) {
        dataItem = dataItem[index[i]];
        dataItem = i < len - 1 ? dataItem.items : dataItem;
    }
    if ((dataItem.value).length == 6 && (dataItem.value).indexOf('Health') == 0) {
        return;
    } else if ((dataItem.value).length == 5
        && dataItem.value.indexOf('Alert') == 0) {
        return;
    } else {
        menuIndexes[3] = dataItem;

        menuItemSelected[3] = dataItem.text;
        listOfMetricNames[3] = dataItem.metricType;
        $("#" + chartheaddiv[3]).val(dataItem.value);

    }

    var menu1 = (dataItem.value).replace(/ /g, '');
    DrawChart(combodiv[13], menu1, (dataItem.charttype));
}

/** For converting JSON file to JSON String */
function srReadFiles1(jsonname) {
    var d = false;
    var jsondata = $.ajax({
        url : jsonname,
        dataType : 'json',
        global : false,
        async : false,
        success : function(data) {
            d = true;
            return data;
        },
        error : function(hrx) {
            d = false;
        }
    }).responseText;
    if (d) {
        return eval(jsondata);
    } else {
        return false;
    }

}

/** For Generating Menu Heading */
function comboboxesall(div1, div2, div3, div4, combodata) {
    if (selectedtab == "Customer View") {
        selectedtab = "Customer";
    }
    if (selectedtab.indexOf("Business") != -1) {
        if (sub == "")
            selectedJson = "Businessservice";
        else
            selectedJson = sub;
    } else
        selectedJson = selectedtab;
    if (selectedJson == "Server") {
        selectedJson = selectedJson.toLowerCase();
    }
    combodata = srReadFiles1("jsonconfigurations/" + selectedJson
        + "ComboSource.json");
    if (selectedtab == "Customer") {
        if (customerUrl != null) {

            combodata = srReadFiles1("jsonconfigurations/" + customerUrl
                + "ComboSource.json");
        }
    }
    data1 = combodata;
    data2 = combodata;
    data3 = combodata;
    data4 = combodata;

    $(div1).kendoMenu({

        dataSource : data1[0].items,

        select : onSelect1

    });
    $(div2).kendoMenu({

        dataSource : data2[0].items,

        select : onSelect2
    });
    $(div3).kendoMenu({

        dataSource : data3[0].items,

        select : onSelect3
    });
    $(div4).kendoMenu({
        dataSource : data4[0].items,
        select : onSelect4
    });

}

/** For Displaying Red, Green, Grey image For Health in Left Grid */
function generateHealthstatus(ResourceNames, Health) {
    var key="";
    if(secondIteration){
        key = servicefirst + "-" + ResourceNames;
    }else if(thirdIteration){
        key = servicefirst + "-" + servicesecond;
    } else if(fourthIteration){
        key = servicefirst + "-" + servicesecond;
    }
    for ( var p = -1, len = watchdogstatus.length; ++p < len;) {
        if(watchdogstatus[p] == key){
            return "<img src = 'images/Grey_logo.png' title = 'No Data' />";
        }
    }
    if (checkAdapterProgress && JSON.stringify(Health).indexOf("CRITICAL") > 0){
        return "<img src = 'images/Red_logo.png'  title = 'Health Violated The Threshold' />";
    }
    if (checkAdapterProgress && JSON.stringify(Health).indexOf("OK") > 0
        && JSON.stringify(Health).indexOf("CRITICAL") == -1){
        return "<img src = 'images/Green_logo.png' title = 'Health OK' />";
    }
    else{
        return "<img src = 'images/Grey_logo.png' title = 'No Data' />";
    }
}

/** For Displaying Red, Green, Grey image For Alert in Left Grid */
function generateAlertstatus(hstdwnstatus) {
    if (JSON.stringify(hstdwnstatus).indexOf("CRITICAL") > 0)
        return "<img src = 'images/Red_logo.png' title = 'Down Event occurred' />";
    else
        return "<img src = 'images/Green_logo.png' title = 'No Down Event' />";
}

function generateLeftGridBasedOnHealth(Health, ServerName) {

    var serv = "<label style='color:white;font-size:12px'  >" + ServerName
    + "</label>";
    return serv;

}

/** Change Left Grid Row text */
function generateLeftGrid(tabName, IpAddress, tabUniqueId) {
    if (tabName != 'server' && tabName != 'Desktop') {
        return IpAddress + "(" + tabUniqueId + ")";
    } else {
        $.inArray(IpAddress, serverDownList) == -1 ? checkAdapterProgress = true
        : checkAdapterProgress = false;
        return IpAddress;
    }
}

/** For Creating Left Grid */
function loadgrid(division5, gridurl, gridtitle) {

    serverDownList.length = 0;
    //var linechartdata=srReadFiles1(customerID  + "/" + $("#datepicker").val() + "/Hour/WatchDogAlert.json");
    var linechartdata=srReadFiles1(customerID  + "/" + $("#datepicker").val() + "/WatchDogAlert.json");
    //check for data
    for(var c=0;c<linechartdata.length;c++){
        if(linechartdata[c].ResourceType==selectedtab.toLowerCase() ||
            linechartdata[c].ResourceType==selectedtab)
            serverDownList.push(linechartdata[c].ServerName);
    }
    displayWatchDog();
    $(division5).empty();
    dataSource = srReadFiles1(gridurl);

    // gridDataSource = dataSource;
    data1 = dataSource;
    $(division5)
    .kendoGrid(
    {
        dataSource : dataSource,
        filterable : true,
        resizable : true,
        selectable : "single",
        sortable : true,
        reorderable : true,
        scrollable : {
            virtual : true
        },

        dataBound : function() {

            filenames = srReadFiles1(combourl);
            comboboxesall(combodiv[0], combodiv[1],
                combodiv[2], combodiv[3], filenames);
            var ip_split = $('#selectedIp').val().split(',');

            if (ip_split[1] != undefined) {
                var selected = $(division5).data("kendoGrid").table
                .find("tr");
                for ( var j = -1, len = selected.length; ++j < len;) {
                    if (selectedtab == "Server"
                        || selectedtab == "server"
                        || selectedtab == "Desktop") {
                        if ($(selected[j]).text().trim() == host
                            || $(selected[j]).text().trim() == ip_split[1]
                            .trim()) {
                            this.select($(selected[j]));
                            break;
                        }
                    } else {
                        if ($(selected[j]).text().trim() == host
                            + "(" + JResourceId + ")"
                            || $(selected[j]).text().trim() == ip_split[1]
                            + "("
                            + ip_split[0]
                            + ")") {
                            this.select($(selected[j]));
                            break;
                        }

                    }
                }

            } else
                this.select(this.tbody.find(">tr:first"));
        },
        change : onChange,
        columns : [
        {
            field : "ServerName",
            title : gridtitle,
            template : "#= generateLeftGridBasedOnHealth(ResourceType,ServerName,ResourceID)#",
            filterable : true,
            width : "120px"
        },

        {
            field : "Health",
            title : "<font color=737C7D ><b>HEALTH</b></font>",
            template : "#= generateHealthstatus(ResourceNames,Health)#",
            filterable : false

        },
        {
            field : "Alert",
            title : "<font color=737C7D ><b>ALERT</b></font>",
            template : "#= generateAlertstatus(hstdwnstatus)#",
            filterable : false

        }

        ]

    });
    /** For Displaying no-data Image */
    if (JSON.stringify(dataSource) == "[]") {
        setNoImage();
    }
}

function setNoImage() {
    $(combodiv[10]).css({
        "background" : "white",
        "background-image" : "url(images/nodata.png)",
        "background-repeat" : "no-repeat",
        "background-position" : "center 30%"
    });
    $(combodiv[11]).css({
        "background" : "white",
        "background-image" : "url(images/nodata.png)",
        "background-repeat" : "no-repeat",
        "background-position" : "center 30%"
    });
    $(combodiv[12]).css({
        "background" : "white",
        "background-image" : "url(images/nodata.png)",
        "background-repeat" : "no-repeat",
        "background-position" : "center 30%"
    });
    $(combodiv[13]).css({
        "background" : "white",
        "background-image" : "url(images/nodata.png)",
        "background-repeat" : "no-repeat",
        "background-position" : "center 30%"
    });
}

/**
 * On Grid load this function will call for Business View and for other included
 * in Grid itself
 */
function onDataBound() {
    if (firstIteration) {
        customerUrl = "Customer";
        Customer = null;
    }
    if (secondIteration) {
        customerUrl = "Installation";
        Customer = servicefirst;
    }
    if (thirdIteration) {
        customerUrl = "Installation";
        Customer = servicefirst + "/" + servicesecond;
    }
    if (fourthIteration) {
        customerUrl = servicethird;
        Customer = servicefirst + "/" + servicesecond + "/" + servicethird;
    }
    filenames = srReadFiles1(combourl);

    comboboxesall(combodiv[0], combodiv[1], combodiv[2], combodiv[3], filenames);

    this.select(this.tbody.find(">tr:first"));

}

function onChangeOfMenu() {
    var common_ResourceChartType = [];
    common_ResourceChartType = selected_rowrecord.ResourceChartType;

    criticalMetricList = "";
    for ( var c = 0; c < selected_rowrecord.Health.length; c++) {
        if (selected_rowrecord.Health[c] == "CRITICAL") {
            criticalMetricList = criticalMetricList + ","
            + selected_rowrecord.ResourceNames[c];
        }
    }
    var originalmenu = data1;

    filenames = originalmenu[0].items[0].items;

    filenames1 = originalmenu[0].items[1].items;

    var w = parseInt(common_ResourceChartType[0]);
    var x = parseInt(common_ResourceChartType[1]);

    if (menuChange) {

        if (menuIndexes[0].category == "health") {
            menuItemSelected[0] = filenames[parseInt(menuIndexes[0].index)].value;
            listOfMetricNames[0] = filenames[parseInt(menuIndexes[0].index)].metricType;
            $("#" + chartheaddiv[0]).val(menuItemSelected[0]);
            DrawChart(combodiv[10], (menuItemSelected[0]).replace(/ /g, ''),
                filenames[parseInt(menuIndexes[0].index)].charttype);
        } else {
            menuItemSelected[0] = filenames1[parseInt(menuIndexes[0].index)].value;
            listOfMetricNames[0] = filenames1[parseInt(menuIndexes[0].index)].metricType;
            $("#" + chartheaddiv[0]).val(menuItemSelected[0]);
            DrawChart(combodiv[10], (menuItemSelected[0]).replace(/ /g, ''),
                filenames1[parseInt(menuIndexes[0].index)].charttype);
        }
        if (menuIndexes[1].category == "health") {
            menuItemSelected[1] = filenames[parseInt(menuIndexes[1].index)].value;
            listOfMetricNames[1] = filenames[parseInt(menuIndexes[1].index)].metricType;
            $("#" + chartheaddiv[1]).val(menuItemSelected[1]);
            DrawChart(combodiv[11], (menuItemSelected[1]).replace(/ /g, ''),
                filenames[parseInt(menuIndexes[1].index)].charttype);
        } else {
            menuItemSelected[1] = filenames1[parseInt(menuIndexes[1].index)].value;
            listOfMetricNames[1] = filenames1[parseInt(menuIndexes[1].index)].metricType;
            $("#" + chartheaddiv[1]).val(menuItemSelected[1]);
            DrawChart(combodiv[11], (menuItemSelected[1]).replace(/ /g, ''),
                filenames1[parseInt(menuIndexes[1].index)].charttype);

        }
        if (menuIndexes[2].category == "health") {
            menuItemSelected[2] = filenames[parseInt(menuIndexes[2].index)].value;
            listOfMetricNames[2] = filenames[parseInt(menuIndexes[2].index)].metricType;
            $("#" + chartheaddiv[2]).val(menuItemSelected[2]);
            DrawChart(combodiv[12], (menuItemSelected[2]).replace(/ /g, ''),
                filenames[parseInt(menuIndexes[2].index)].charttype);

        } else {
            menuItemSelected[2] = filenames1[parseInt(menuIndexes[2].index)].value;
            listOfMetricNames[2] = filenames1[parseInt(menuIndexes[2].index)].metricType;
            $("#" + chartheaddiv[2]).val(menuItemSelected[2]);
            DrawChart(combodiv[12], (menuItemSelected[2]).replace(/ /g, ''),
                filenames1[parseInt(menuIndexes[2].index)].charttype);

        }
        if (menuIndexes[3].category == "health") {
            menuItemSelected[3] = filenames[parseInt(menuIndexes[3].index)].value;
            listOfMetricNames[3] = filenames[parseInt(menuIndexes[3].index)].metricType;
            $("#" + chartheaddiv[3]).val(menuItemSelected[3]);
            DrawChart(combodiv[13], (menuItemSelected[3]).replace(/ /g, ''),
                filenames[parseInt(menuIndexes[3].index)].charttype);

        } else {

            menuItemSelected[3] = filenames1[parseInt(menuIndexes[3].index)].value;
            listOfMetricNames[3] = filenames1[parseInt(menuIndexes[3].index)].metricType;
            $("#" + chartheaddiv[3]).val(menuItemSelected[3]);
            DrawChart(combodiv[13], (menuItemSelected[3]).replace(/ /g, ''),
                filenames1[parseInt(menuIndexes[3].index)].charttype);

        }

    } else {

        menuIndexes[0] = filenames[w];

        menuIndexes[1] = filenames[x];

        menuIndexes[2] = filenames1[1];

        menuIndexes[3] = filenames1[2];

        // alert("from else"+JSON.stringify(menuIndexes))
        menuItemSelected[0] = filenames[w].value;
        listOfMetricNames[0] = filenames[w].metricType;
        menuItemSelected[1] = filenames[x].value;
        listOfMetricNames[1] = filenames[x].metricType;
        menuItemSelected[2] = filenames1[2].value;
        listOfMetricNames[2] = filenames1[2].metricType;
        menuItemSelected[3] = filenames1[1].value;
        listOfMetricNames[3] = filenames1[1].metricType;
        $("#" + chartheaddiv[0]).val(menuItemSelected[0]);
        $("#" + chartheaddiv[1]).val(menuItemSelected[1]);
        $("#" + chartheaddiv[2]).val(menuItemSelected[2]);
        $("#" + chartheaddiv[3]).val(menuItemSelected[3]);

        DrawChart(combodiv[10], (filenames[w].text).replace(/ /g, ''),
            (filenames[w].charttype));
        DrawChart(combodiv[11], (filenames[x].text).replace(/ /g, ''),
            (filenames[x].charttype));
        DrawChart(combodiv[12], (filenames1[2].text).replace(/ /g, ''),
            (filenames1[2].charttype));
        DrawChart(combodiv[13], (filenames1[1].text).replace(/ /g, ''),
            (filenames1[1].charttype));
    }
    //var linechartdata=srReadFiles1(customerID  + "/" + $("#datepicker").val() + "/Hour/WatchDogAlert.json");
    var linechartdata=srReadFiles1(customerID  + "/" + $("#datepicker").val() + "/WatchDogAlert.json");
    //check for data
    for(var c=0;c<linechartdata.length;c++){
        if(linechartdata[c].ResourceType==selectedtab.toLowerCase() ||
            linechartdata[c].ResourceType==selectedtab)
            serverDownList.push(linechartdata[c].ServerName);
    }
    displayWatchDog();
}

/**
 * On Changing the Row of the grid this function will call for all tab except
 * SLA View
 */
function onChange() {
    var grid = this;
    selectedTheme = "highcontrast";
    grid.collapseRow(grid.tbody.find("tr.k-master-row"));
    $('#loader').html('<img src="images/loading.gif">');
    $('#loader').show();

    if (selectedtab == "Customer View") {
        selectedtab = "Customer";
    }
    selected_rowrecord = this.dataSource.view()[this.select().index()];

    if (selectedtab == "Customer") {

        $.map(this.select(), function(item) {
            selectedIP = $(item).index();

            if (fourthIteration) {

                hostUrl = Customer;
                host = selected_rowrecord.ServerName;
                JResourceId = selected_rowrecord.ResourceID;

            } else if (firstIteration) {

                hostUrl = selected_rowrecord.Customer;
                host = selected_rowrecord.Customer;
                JResourceId = selected_rowrecord.Customer;

            } else {
                hostUrl = Customer + "/" + selected_rowrecord.ServiceName;
                host = selected_rowrecord.ServiceName;
                JResourceId = selected_rowrecord.ServiceName;
            }

        });
    } else {

        $.map(this.select(), function(item) {
            selectedIP = $(item).index();

            $('#selectedIp').val(
                selected_rowrecord.ResourceID + ","
                + selected_rowrecord.ServerName);
            document.cookie = 'selectedHostCookies = '
            + selected_rowrecord.ResourceID + ","
            + selected_rowrecord.ServerName;
        });
        host = selected_rowrecord.ServerName;
        JResourceId = selected_rowrecord.ResourceID;
    }
    onChangeOfMenu();

}

/** This function will call on Series Click and creating kendoGrid with data */

function summarywindow(sumarydata) {

    var summaryHeadingNames = new Array();
    var window2 = $("#summarywindow");
    window2
    .kendoWindow(
    {
        width : $(window).width() - 10,
        modal : true,
        resizable : false,
        close : function() {
            // grid1.remove();
            window2 = null;
        },
        open : function() {
            $("#summarywindow").html(
                '<div id="summarygrid"></div>');
            $("#summarygrid")
            .kendoGrid(
            {
                dataSource : new kendo.data.DataSource(
                {
                    transport : {
                        read : {
                            url : "PopupController",
                            dataType : "json"
                        },
                        parameterMap : function(
                            options,
                            operation) {
                            return sumarydata;
                        }
                    },
                    pageSize : 20,
                    schema : {
                        data : function(
                            data) {
                            var sRealMetricType = '';
                            if (data[0].MetricType)
                                sRealMetricType = data[0].MetricType;
                            else
                                sRealMetricType = sumarydata.metrictype;

                            for ( var key in data[0]) {
                                summaryHeadingNames
                                .push({
                                    field : key
                                });
                            }
                            var positioningTitle = ((sRealMetricType.length + 9) * 5 / 2) + 100;
                            $(
                                "#summarywindow")
                            .css(
                                'min-height',
                                '100px');
                            $(
                                '.k-window')
                            .css(
                                'top',
                                '5px');
                            $(
                                '.k-window-title')
                            .html(
                                '<input type = "button"  onclick = "exportToXls()" title = "Export to XLS"	style = "background-image: url(images/exportXLS.png);cursor:pointer;" /> <input type = "button"  onclick = "exportToPdf()" title = "Export to PDF" style = "background-image: url(images/exportPDF.png);cursor:pointer;margin-right:'
                                + (($(
                                    window)
                                .width() / 2) - positioningTitle)
                                + 'px;" />Summary('
                                + sRealMetricType
                                .charAt(
                                    0)
                                .toUpperCase()
                                + sRealMetricType
                                .slice(1)
                                + ')');

                            return data;
                                total: data.length;
                        }
                    }
                }),

                serverPaging : true,
                serverSorting : true,
                filterable : true,
                sortable : true,
                resizable : true,
                pageable : true,
                height : $(window).height() - 55,
                scrollable : true,
                columns : summaryHeadingNames

            });
            $('.k-grid-pager').css('font-size', '8px');
        }
    }).data("kendoWindow").center().open();

}

/** In Summary window, For Downloading XL Sheet */
function exportToXls() {
    window.location.href = "DownloadMetrics?type=xls&comboboxselected="
    + chartHeading;
}

/** In Summary window, For Downloading PDF */
function exportToPdf() {
    window.location.href = "DownloadMetrics?type=pdf&comboboxselected="
    + chartHeading;
}

/** Dynamically Adjusting Window Size */
function displayAdjust() {
    $(combodiv[8]).width($(window).width() - 25);
    $(combodiv[8]).height($(window).height() - 75);
    $(combodiv[9]).width(3 * ($(combodiv[8]).width()) / 4 - 15);
    $(combodiv[9]).height($(combodiv[8]).height() - 20);
    $('#liveWindow').width(3 * ($(combodiv[8]).width()) / 4 - 15);
    $('#liveWindow').height($(combodiv[8]).height() - 20);
    $(combodiv[14]).width(1 * ($(combodiv[8]).width()) / 4 + 10);
    $(combodiv[14]).height($(combodiv[9]).height() + 20);
    $(combodiv[10]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[11]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[12]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[13]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[4]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[5]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[6]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[7]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[10]).height(($(combodiv[9]).height()) / 2 - 20);
    $(combodiv[11]).height(($(combodiv[9]).height()) / 2 - 20);
    $(combodiv[12]).height(($(combodiv[9]).height()) / 2 - 20);
    $(combodiv[13]).height(($(combodiv[9]).height()) / 2 - 20);
    $(combodiv[4]).height(($(combodiv[9]).height()) / 20);
    $(combodiv[5]).height(($(combodiv[9]).height()) / 20);
    $(combodiv[6]).height(($(combodiv[9]).height()) / 20);
    $(combodiv[7]).height(($(combodiv[9]).height()) / 20);

    leftpanwidth = 3 * ($(combodiv[8]).width()) / 4 - 15;
    rigthpanwidth = ($(combodiv[8]).width()) / 4 + 10;
    leftpanwidth = leftpanwidth + "px";
    rigthpanwidth = rigthpanwidth + "px";

}

/**
 * On Click Refresh Button on Right side of web page. This will fetch current
 * JSON File and Generate chart again
 */
function refreshbutton() {
    menuChange = true;
    if (selectedtab.trim() == "Business View") {

        gridurl = customerID + "/" + $('#maincombo').val() + "/"
        + "Defaultservice.json";
        $(combodiv[0]).empty();
        $(combodiv[1]).empty();
        $(combodiv[2]).empty();
        $(combodiv[3]).empty();

        maingrid(combodiv[14], gridurl, gridtitle);
    } else if (selectedtab.trim() == "SLA View") {
        displayArrange();
        TimeLinegrid();
    } else if (selectedtab.trim() == "Customer View") {

        onChangeOfMenu();

    } else {
        $(combodiv[10]).empty();
        $(combodiv[11]).empty();
        $(combodiv[12]).empty();
        $(combodiv[13]).empty();

        var gridus = gridurl.split("/");
        gridurl = "";
        for ( var i = 2; i < gridus.length; i++) {
            gridurl = gridurl + "/" + gridus[i];
        }
        gridurl = customer + gridurl;

        loadgrid(combodiv[14], gridurl, gridtitle);
    // menuHower(menuHeading);
    }
    $('#loader').hide();
/*
     * if(selectedtab.trim() == "Network"){ $('#marqueeLabel').html(''); } else{
     */
// displayWatchDog(linechartdata);
/* } */
}

/**
 * This will Display Moving Flash Watch Dog below the Page on Click Refresh and
 * on tab with TimeStamp
 */
/*
 * function displayWatchDogOld(linechartdata) { var checkAdapterProgress = true;
 * var dates=[]; displayAdapterInformation = ""; if(linechartdata == undefined)
 * linechartdata = []; for(var i = -1, len = linechartdata.length; ++i < len;) {
 * for(var j = -1,len1 = linechartdata[i].ResourceNames.length; ++j < len1;){
 * var q=0; adapterName.reduce(function(keys, element){ for (key in element) {
 * if(linechartdata[i].ServerName != 'null' &&
 * JSON.stringify(linechartdata[i].ResourceNames[j]).indexOf(key) != -1) {
 * for(var p = -1, len2 = linechartdata[i].TimeStamps[j].length; ++p < len2;){
 * 
 * dates.push(new Date(linechartdata[i].TimeStamps[j][p])); } dString = new
 * Date(Math.max.apply(null,dates)).toString(); var gangaliaDownedTimestamp =
 * dString.substring(0, dString.indexOf('GMT', 0)); gangaliaDownedServerName =
 * linechartdata[i].ServerName; var NameOfMonitoringAgent=adapterName[q][key];
 * displayAdapterInformation = displayAdapterInformation.trim()+";
 * "+gangaliaDownedServerName+" "+ NameOfMonitoringAgent +" Monitoring Agent
 * Down at "+gangaliaDownedTimestamp; checkAdapterProgress = false; break; }
 * else{ checkAdapterProgress = true; } q++; } },[]); } for(var c = 0;c <
 * adapterName.length; c++) if(linechartdata[i].ServerName != 'null' &&
 * JSON.stringify(linechartdata[i].ResourceNames[j]).indexOf(adapterName[c]) !=
 * -1) { for(var p=0;p<linechartdata[i].TimeStamps[j].length;p++){
 * 
 * dates.push(new Date(linechartdata[i].TimeStamps[j][p])); } dString = new
 * Date(Math.max.apply(null,dates)).toString(); gangaliaDownedTimestamp =
 * dString.substring(0, dString.indexOf('GMT', 0)); gangaliaDownedServerName =
 * linechartdata[i].ServerName; //var
 * NameOfMonitoringAgent=adapterName[c].get(); var
 * NameOfMonitoringAgent=adapterName[c][linechartdata[i].ResourceNames[j]];
 * displayAdapterInformation = displayAdapterInformation.trim()+";
 * "+gangaliaDownedServerName+" "+ NameOfMonitoringAgent +" Monitoring Agent
 * Down at "+gangaliaDownedTimestamp; checkAdapterProgress = false; } }
 * if(!checkAdapterProgress) { $('#marqueeLabel').css('top',$(window).height() -
 * 30); $('#marqueeLabel').html('<MARQUEE WIDTH=100% BEHAVIOR="SCROLL"
 * onmouseover="this.stop()" onmouseout="this.start()" DIRECTION="LEFT"
 * BGColor=lightgrey>'+displayAdapterInformation.substring(1)+'</MARQUEE>');
 * }else $('#marqueeLabel').html(''); linechartdata = []; }
 */

function displayWatchDog() {
    //var linechartdata = srReadFiles1(customerID  + "/" + $("#datepicker").val() + "/Hour/WatchDogAlert.json");
    var linechartdata = srReadFiles1(customerID  + "/" + $("#datepicker").val() + "/WatchDogAlert.json");
    displayAdapterInformation = "";
    if (linechartdata == undefined)
        linechartdata = [];
    if (selectedtab == "Server")
        selectedtab = "server";

    for ( var i = -1, len = linechartdata.length; ++i < len;) {

        dString = new Date(linechartdata[i].Timestamps).toString();
        gangaliaDownedTimestamp = dString.substring(0, dString
            .indexOf('GMT', 0));
        var gangaliaDownedServerName1 = linechartdata[i].ResourceType;
        var ServerName = linechartdata[i].ServerName;
        var inst = linechartdata[i].ResourceID;
        gangaliaDownedServerName = gangaliaDownedServerName1.charAt(0)
        .toUpperCase()
        + gangaliaDownedServerName1.slice(1);
        displayAdapterInformation = displayAdapterInformation.trim() + ";  "
        /*+ gangaliaDownedServerName + " Monitoring Agent Down on "
        + ServerName + " at " + gangaliaDownedTimestamp;*/
        + "Monitoring Agent Down at " + ServerName +"/"+ inst + " at " + gangaliaDownedTimestamp;

        watchdogstatus = [];
        watchdogstatus.push(ServerName + "-" + inst);

    }
    if (selectedtab == "server")
        selectedtab = "Server";
    if (displayAdapterInformation.indexOf(selectedtab) != -1) {
        checkAdapterProgress = false;
    } else {
        checkAdapterProgress = true;
    }

    if (displayAdapterInformation != "") {
        $('#marqueeLabel').css('top', $(window).height() - 30);
        $('#marqueeLabel')
        .html(
            '<MARQUEE WIDTH=100% BEHAVIOR="SCROLL" onmouseover="this.stop()" onmouseout="this.start()" DIRECTION="LEFT" BGColor=lightgrey>'
            + displayAdapterInformation.substring(1)
            + '</MARQUEE>');
    } else
        $('#marqueeLabel').html('');
    linechartdata = [];
    if (selectedtab == "Server")
        selectedtab = "server";
}

function shortLabels(labelString) {
    Date.prototype.monthNames = [ "January", "February", "March", "April",
    "May", "June", "July", "August", "September", "October",
    "November", "December" ];
    if (JSON.stringify(resourceName).indexOf("Availability") != -1
        && inc < linetimestamps.length) {
        labelString = availTimestamps[inc];
        inc++;
        labelString = labelString.toString();
    }

    Date.prototype.getMonthName = function() {
        return this.monthNames[this.getMonth()];
    };
    Date.prototype.getShortMonthName = function() {
        return this.getMonthName().substr(0, 3);
    };
    if (labelString.toString().indexOf(":") != -1) {
        var graphunits = $("#maincombo").val();
        var valuesplit = labelString;
        var changeFormate = labelString;
        var split_Date = valuesplit.split(" ");
        if (graphunits == "Hour") {
            var value1 = split_Date[1].split(":");
            labelString = value1[0] + ":" + value1[1];

        } else if (graphunits == "Day") {
            /*var value1 = split_Date[0].split("/");
            var date = new Date(changeFormate);
            labelString = value1[2] + " " + date.getShortMonthName();
            repeatedValue = labelString;
            if (pivotValue == labelString) {
                labelString = "";
            }
            pivotValue = repeatedValue;*/
            var value1 = split_Date[1].split(":");
            labelString = value1[0] + ":" + value1[1];
        }else if (graphunits == "Week") {
            var value1 = split_Date[0].split("/");
            var date = new Date(changeFormate);
            labelString = value1[2] + " " + date.getShortMonthName();
            repeatedValue = labelString;
            if (pivotValue == labelString) {
                labelString = "";
            }
            pivotValue = repeatedValue;

        } else if (graphunits == "Month") {
            var value1 = split_Date[0].split("/");
            var date = new Date(changeFormate);
            labelString = value1[2] + " " + date.getShortMonthName();
            repeatedValue = labelString;
            if (pivotValue == labelString) {
                labelString = "";
            }
            pivotValue = repeatedValue;

        } else if (graphunits == "Year") {
            var value1 = split_Date[0].split("/");
            var date = new Date(changeFormate);
            labelString = date.getShortMonthName() + ", " + value1[0];
            repeatedValue = labelString;
            if (pivotValue == labelString) {
                labelString = "";
            }
            pivotValue = repeatedValue;

        }
        return labelString;
    } else {
        //return "";
        return labelString;
    }
}

/** On click Live button, It will generate live lineChart */
function generateLiveLineChart(id) {

    if (id.indexOf('2') > 0) {
        if (menuItemSelected[1].indexOf("Alert") != -1
            || menuItemSelected[1].indexOf("Availability") != -1) {
            alert("Select from Health Menu, Except Availability");
            return;
        } else {
            menuItemSelected[0] = $("#" + chartheaddiv[1]).val();// menuItemSelected[0][1];
            listOfMetricNames[0] = listOfMetricNames[1];
        }
    } else if (id.indexOf('3') > 0) {
        if (menuItemSelected[2].indexOf("Alert") != -1
            || menuItemSelected[2].indexOf("Availability") != -1) {
            alert("Select from Health Menu, Except Availability");
            return;
        } else {
            menuItemSelected[0] = $("#" + chartheaddiv[2]).val();// menuItemSelected[0][2];
            listOfMetricNames[0] = listOfMetricNames[2];
        }
    } else if (id.indexOf('4') > 0) {
        if (menuItemSelected[3].indexOf("Alert") != -1
            || menuItemSelected[3].indexOf("Availability") != -1) {
            alert("Select from Health Menu, Except Availability");
            return;
        } else {
            menuItemSelected[0] = $("#" + chartheaddiv[3]).val();// menuItemSelected[3];
            listOfMetricNames[0] = listOfMetricNames[3];
        }
    } else if (menuItemSelected[0].indexOf("Alert") != -1
        || menuItemSelected[0].indexOf("Availability") != -1) {
        alert("Select from Health Menu, Except Availability");
        return;
    } else {
        menuItemSelected[0] = $("#" + chartheaddiv[0]).val();// menuItemSelected[3];
    }
    var window3 = $("#liveWindow");
    window3.kendoWindow({
        modal : true,
        resizable : true,
        close : function() {
            $("#liveChartdiv").empty();
            window3 = null;
            clearInterval(timeout_interval);
            window.location.reload();
            timeout_interval = null;
        // live_kendo_chart.remove();
        },
        open : function() {
            $("#liveWindow").html('<div id="liveChartdiv"></div>');
            initializeData();
            setTimeout(function() {
                createChartLive();
            }, 400);

        }
    }).data("kendoWindow");
    $('.k-window').css('top', '40px');
    $('.k-window').css('left', (($(window).width() - 25) / 4 + 30));
    $('.k-window-title').html(menuItemSelected[0]);
    $('.k-window-title').css('text-align', 'center');

}

/** Setting Metric names and calling to Generate Chart */
function initializeData() {

    metriclist = "'" + listOfMetricNames[0].toString().replace(/,/g, "','")
    + "'";
    play();
}

/** Creating Live Line Chart */
function createChartLive() {

    //var seriesArrayColor = [ "green", "red", "blue", "yellow", "grey" ];
    $("#liveChartdiv").kendoChart({
        //theme : selectedTheme,
        chartArea: {
            width : 800,
            height : 400
        },
        legend : {
            position : "bottom",
            visible : true
        },
        seriesDefaults : {
            type : "line",
            markers : {
                size : 0
            }
        },
        series : seriesArray,
        //seriesColors : seriesArrayColor,
        valueAxis : {
            labels : {
                template : "#= YLabelShortening(value)#"
            }
        /*
             * labels : { format : "{0}" }
             */
        },
        categoryAxis : {
            categories : categoryList,
            labels : {
                rotation : 90,
                step : 20

            }
        },
        transitions : false,
        tooltip : {
            visible : true,
            template : "#= series.name #= ${value}",
            color: "black"

        }
    });
}

/** This function is used to call Every second by using setInterval */
function play() {
    //timeout_interval = setInterval(addPoint, 1000);
    //modified code
    var frames=0;
    timeout_interval = setInterval(function(){
        step();
        frames++;
    },100);
}

function step() {
    addPoint();
    //$("#chart").data("kendoChart").refresh();
    $("#liveChartdiv").data("kendoChart").refresh();
}

/** Every second adding Values for generating live Chart */
function addPoint() {
    var stockData;
    if (secoundCount == 5 || secoundCount == 1) {
        datasourceLive = getData();

        if (datasourceLive[0] != "[]") {
            xAxisLabelsLive = datasourceLive[0].timestamp.toString().split(':');
            yAxisValuesLive = datasourceLive[0].MetricValue;
            MetricNameLive = datasourceLive[0].MetricName;
        }
        if (secoundCount == 1) {
            if (MetricNameLive.length == 1)
                stocks = [ [] ];
            else if (MetricNameLive.length == 2)
                stocks = [ [], [] ];
            else if (MetricNameLive.length == 3)
                stocks = [ [], [], [] ];
            else if (MetricNameLive.length == 4)
                stocks = [ [], [], [], [] ];
            else if (MetricNameLive.length == 5)
                stocks = [ [], [], [], [], [] ];
            else if (MetricNameLive.length == 6)
                stocks = [ [], [], [], [], [], [] ];
        }
        secoundCount = 1;

    }
    secoundCount++;

    if (xAxisLabelsLive != undefined) {
        var localTime = new Date();
        var hours = localTime.getHours();
        var minutes = localTime.getMinutes();
        var seconds = localTime.getSeconds();

        if (timeFlag) {
            xlablesseconds = (parseInt(xAxisLabelsLive[2], 10) + parseInt(0));
            xlablesMinutes = parseInt(xAxisLabelsLive[1], 10);
            xlablesHours = parseInt(xAxisLabelsLive[0], 10);
            diffTime = ((hours * 3600 + minutes * 60) + seconds)
            - ((parseInt(xlablesHours) * 3600 + parseInt(xlablesMinutes) * 60) + parseInt(xlablesseconds));
            timeFlag = false;
        }

        var actualTime = ((parseInt(hours) * 3600 + parseInt(minutes) * 60) + parseInt(seconds))
        - parseInt(diffTime);

        xhours = actualTime / 3600;
        xrem = actualTime % 3600;
        xminutes = xrem / 60;
        xseconds = xrem % 60;

        xlablesHours = singleToDoubleDigit(parseInt(xhours));
        xlablesseconds = singleToDoubleDigit(parseInt(xseconds));
        xlablesMinutes = singleToDoubleDigit(parseInt(xminutes));

        categoryList.push(xlablesHours + ":" + xlablesMinutes + ":"
            + xlablesseconds);
        if (categoryList.length > POINTS) {
            categoryList.shift();
        }
        for ( var i = 0; i < stocks.length; i++) {
            stockData = stocks[i];
            if(first){
                seriesArray.push({
                    name : MetricNameLive[i],
                    data : stocks[i],
                    width : 1
                });
            }
            else{
                seriesArray.push({
                    //name : MetricNameLive[i],
                    data : stocks[i],
                    width : 1
                });
            }
            stockData.push(parseFloat(yAxisValuesLive[i]));
            if (stockData.length > POINTS) {
                stockData.shift();
            }
        }
        first = false;
    }
    /*
     * alert(JSON.stringify(seriesArray)); alert(xAxisLabelsLive);
     */
    if (xAxisLabelsLive != undefined) {
        $("#liveChartdiv").data("kendoChart").redraw();
        $("#liveChartdiv").data("kendoChart").refresh();
    //createChartLive();
    //$("#liveChartdiv").data("kendoChart").refresh();
    } /*else {
		$("#liveChartdiv").data("kendoChart").refresh();

	}*/
}
/** calling Servlet and get Current data for generating live Chart every 30sec */
function getData() {

    var ajax_datasource_live = $.ajax({
        url : 'TestLineChart',
        type : 'GET',
        dataType : 'json',
        data : {
            HostName : host.replace(/ /g, ''),
            ResourceId : JResourceId.replace(/ /g, ''),
            MetricsList : listOfMetricNames[0].toString().replace(/ /g, '')
        },
        async : false,
        success : function(data) {

        }
    }).responseText;

    return eval(ajax_datasource_live);

}

/** Function For Converting Single digit to Double digit */
function singleToDoubleDigit(a) {
    return (a + "").length == 1 ? "0".concat(a) : a;
}

/** Not Used */
function menuHower(menuHeading1) {
    menuHeading = menuHeading1;
    $("#" + menuHeading1.toLowerCase() + " ul div").css('width', '98%');
    $("#" + menuHeading1.toLowerCase() + " ul div").closest('ul').css('width',
        '98%');

}

/** Not Used */
function onExpand(e) {

    displayAdjust();
    $(combodiv[10]).data("kendoChart").redraw();
    $(combodiv[11]).data("kendoChart").redraw();
    $(combodiv[12]).data("kendoChart").redraw();
    $(combodiv[13]).data("kendoChart").redraw();
}

/** Not Used */
function onCollapse(e) {

    $(combodiv[9]).width($(window).width() - 25);
    $(combodiv[9]).height($(window).height() - 90);
    $(combodiv[14]).width(1 * ($(combodiv[8]).width()) / 4 + 10);
    $(combodiv[14]).height($(combodiv[9]).height() + 20);
    $(combodiv[10]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[11]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[12]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[13]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[4]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[5]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[6]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[7]).width(($(combodiv[9]).width()) / 2 - 8);
    $(combodiv[10]).height(($(combodiv[9]).height()) / 2 - 20);
    $(combodiv[11]).height(($(combodiv[9]).height()) / 2 - 20);
    $(combodiv[12]).height(($(combodiv[9]).height()) / 2 - 20);
    $(combodiv[13]).height(($(combodiv[9]).height()) / 2 - 20);
    $(combodiv[4]).height(($(combodiv[9]).height()) / 20);
    $(combodiv[5]).height(($(combodiv[9]).height()) / 20);
    $(combodiv[6]).height(($(combodiv[9]).height()) / 20);
    $(combodiv[7]).height(($(combodiv[9]).height()) / 20);
    $(combodiv[10]).data("kendoChart").redraw();
    $(combodiv[11]).data("kendoChart").redraw();
    $(combodiv[12]).data("kendoChart").redraw();
    $(combodiv[13]).data("kendoChart").redraw();
}

/** Not Used */
function commonsplitter(splitterdivison) {
    displayAdjust();
    $(splitterdivison).kendoSplitter({
        orientation : "horizontal",
        panes : [ {
            collapsible : true,
            size : rigthpanwidth,
            resizable : false
        }, {
            collapsible : false,
            size : leftpanwidth,
            resizable : false
        },

        ],
        collapse : onCollapse,
        expand : onExpand

    });

    loadgrid(combodiv[14], gridurl, gridtitle);
}

function DrawChart(divName, menu, charttype) {

    if (charttype.indexOf("lineargauge") != -1) {
        DrawLinearGauge(divName, menu);
    } else if (charttype.indexOf("column") != -1) {

        $(document).bind("kendo:skinChange", DrawColumnChart(divName, menu));
    } else if (charttype.indexOf("grouped") != -1) {
        if(secondIteration || fourthIteration){
            $(document).bind("kendo:skinChange",
                DrawGroupedColumnChart(divName, menu));
        }else{
            $(document).bind("kendo:skinChange",
                DrawGroupedColumnChart(divName, menu));
        }
    } else if (charttype.indexOf("line") != -1) {

        DrawLineChart(divName, menu);
    } else if (charttype.indexOf("table") != -1) {

        DrawTable(divName, menu);
    }

}

/* draw table using kendo grid */
function DrawTable(division1, menuselelected) {
    $(division1).empty();
    $(division1).css("background-image", "");
    if (selectedtab == "Customer View") {
        selectedtab = "Customer";
    }

    var linechartdataforip;

    if (selectedtab == "Customer") {
        linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
            + menuselelected + ".json");

        if (thirdIteration) {

            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + menuselelected + ".json");
        }

        if (fourthIteration) {
            if(menuselelected == "Irradiation"){
                linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                    + "Inverter"+menuselelected + ".json");
            }else{
                linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/" + servicethird + "Summary.json");
            }
        }

    } else {

        linechartdataforip = srReadFiles1(customer + "/" + selectedtab + "/"
            + menuselelected + "/" + selectedtab + "MetricTypes.json");

    }
    
    var data, data1;
    
    if(menuselelected == "Weather"){
        data = linechartdataforip[0].Now;
        data1 = linechartdataforip[1].Forecast;
        if (data.length != 0) {
            $("<label>Now:</label>").appendTo(division1);
            $(division1).kendoGrid({

                dataSource : data,
                scrollable : false,
                resizable : false,
                reorderable : false,
                sortable : false,
                columns : [ {
                    template : "#=gridTable(name)#",
                    width : "125px",
                    attributes:{
                        style:"text-align:left;"
                    }
                }, {
                    template : "#=gridTable2(value,unit)#",
                    width : "125px",
                    attributes:{
                        style:"text-align:right;"
                    }
                } ]

            }).css("background-color", "#2C232B");
            if (data1.length != 0) {

                $("<label>Forecast:</label>").appendTo(division1).css(
                    "background-color", "#2C232B");
                $("<div/>").appendTo(division1).kendoGrid({
                    dataSource : data1,
                    scrollable : false,
                    resizable : false,
                    reorderable : false,
                    sortable : false,

                    columns : [ {
                        template : "#=gridTable(name1)#"
                    }, {
                        template : "#=gridTable(name2)#"
                    }, {
                        template : "#=gridTable(name3)#"
                    }, {
                        template : "#=gridTable(name4)#"
                    }, {
                        template : "#=gridTable(name5)#"
                    }, {
                        template : "#=gridTable(name6)#"
                    }, {
                        template : "#=gridTable(name7)#"
                    }, ]

                }).css("background-color", "#2C232B");
            }
            hideHeader();
        } else {
            DisplayNoDataImage(division1);
        }
    }else if(menuselelected.indexOf("Summary") > -1){
        if (fourthIteration) {
            for ( var p = -1, len1 = linechartdataforip.length; ++p < len1;) {
                if (linechartdataforip[p].ResourceID == host) {
                    data = linechartdataforip[p].Value;
                    break;
                }
            }
        }else if(firstIteration){
            var rows = [];
            var resArray = [];
            resArray.push("Metric");
            for (var j = 0; j <linechartdataforip.length; j++) {
                resArray.push(linechartdataforip[j].ResourceID);
            }	
            if(linechartdataforip.length == 0){
                DisplayNoDataImage(division1);
                return;
            }
            for (var i = 0; i < linechartdataforip[0].Value.length; i++) {
                var entryArray = [];
                entryArray.push(linechartdataforip[0].Value[i].name + " " + linechartdataforip[0].Value[i].unit );
                for (var j = 0; j < linechartdataforip.length; j++) {
                    var valArray = [];
                    //valArray.push("Now: " + linechartdataforip[j].Value[i].curvalue + "          ");
                    valArray.push(linechartdataforip[j].Value[i].curvalue + "          ");
                    //valArray.push( "Min: " + linechartdataforip[j].Value[i].minvalue + '\n');
                    //valArray.push("Max: " + linechartdataforip[j].Value[i].maxvalue + '\n');
                    //valArray.push("Avg: " + linechartdataforip[j].Value[i].avgvalue);
                    entryArray.push(valArray);
                }
                rows.push(kendo.observable({	
                    entries: entryArray
                }));
            }
            var columns = [];
            for (var i = 0; i < resArray.length; i++) {
                var entryIndex = "entries[" + i + "]";
                columns.push({
                    field: entryIndex,
                    title: resArray[i],
                    width : '100px',
                    attributes:{
                        style:"text-align:left;"
                    }
                });
            }

            var configuration = {
                editable:false,
                sortable:true,
                scrollable: true,	
                columns: columns,
                //width: 3000,
                dataSource: rows
            };

            var timeEditGrid = $(division1).kendoGrid(configuration).data("kendoGrid");
        }else{
            data = linechartdataforip;
        }	
        if(!firstIteration){        
            if (data.length != 0) {
                /*                $("<label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;Current&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; Min &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;Max&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;Avg</label>").appendTo(division1);*/
                $("<label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;Current&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;</label>").appendTo(division1);
                $(division1).kendoGrid({

                    dataSource : data,
                    scrollable : true,
                    resizable : false,
                    reorderable : false,
                    sortable : false,
                    columns : [ {
                        title : "Metric",
                        template : "#=gridTable2(name,unit)#",
                        width : "125px",
                        attributes:{
                            style:"text-align:left;"
                        }
                    }, {
                        title : "Current",
                        template : "#=gridTable(curvalue)#",
                        width : "75px",
                        attributes:{
                            style:"text-align:right;"
                        }
                    }
                    /*, {                
                        title : "Min",    
                        template : "#=gridTable(minvalue)#",
                        width : "75px",
                        attributes:{
                            style:"text-align:right;"
                        }
                    }, {
                        title : "Max",
                        template : "#=gridTable(maxvalue)#",
                        width : "75px",
                        attributes:{
                            style:"text-align:right;"
                        }
                    }, {
                        title : "Avg",
                        template : "#=gridTable(avgvalue)#",
                        width : "75px",
                        attributes:{
                            style:"text-align:right;"
                        }
                    } */
                    ]

                }).css("background-color", "#2C232B");
                hideHeader();
            } else {
                DisplayNoDataImage(division1);
            }
        }
    }else if(menuselelected.indexOf("Irradiation") > -1){
        data = linechartdataforip;
        if (data.length != 0) {
            $("<label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;"+
                "&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;"+
                "&nbsp;TimeStamp&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;"+
                "&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;"+
                "&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;Irradiation</label>").appendTo(division1);
            $(division1).kendoGrid({

                dataSource : data,
                scrollable : true,
                resizable : false,
                reorderable : false,
                sortable : false,
                columns : [ {
                    title : "Timestamp",
                    template : "#=gridTable(timestamp)#",
                    width : "125px",
                    attributes:{
                        style:"text-align:left;"
                    }
                }, {
                    title : "Irradiation",
                    template : "#=gridTable(IrradiationValue[0].cumm)#",
                    width : "75px",
                    attributes:{
                        style:"text-align:right;"
                    }
                }]

            }).css("background-color", "#2C232B");
            hideHeader();
        } else {
            DisplayNoDataImage(division1);
        }
    }

    

    

    if (selectedtab == "Customer") {
        selectedtab = "Customer View";
    }

}
function gridTable(value) {
    return value;
}
function gridTable2(value, unit) {
    return value +  " " + unit ;
}

function DisplayNoDataImage(DivID) {
    $('#loader').hide();
    $(DivID).empty();
    $(DivID).css({

        "background-image" : "url(images/nodata.png)",
        "background-repeat" : "no-repeat",
        "background-position" : "center 30%"
    });
}
function lineSeriesClick(menuselelected, seriesName) {
    var reqdata;
    if (selectedtab == "Server") {
        selectedtab = "server";
    }
    if (selectedtab == "Business View") {
        tempSelectedtab = selectedtab;
        selectedtab = sub;
    }
    if (selectedtab == "Customer View") {
        tempSelectedtab = selectedtab;
        /*if (firstIteration) {

			selectedtab = servicefirst;
		}
		if (secondIteration) {
			selectedtab = servicesecond;
		}
		if (thirdIteration) {
			selectedtab = servicethird;
		}
		if (fourthIteration) {

			selectedtab = servicethird;

		}*/
        if (firstIteration) {

            tempSelectedtab = servicefirst;
        }
        if (secondIteration) {
            tempSelectedtab = servicesecond;
        }
        if (thirdIteration) {
            tempSelectedtab = servicethird;
        }
        if (fourthIteration) {
            tempSelectedtab = servicethird;
        }
        reqdata = {
            hostName : host,
            metrictype : seriesName,
            resourcetype : tempSelectedtab,
            ComboSelected : menuselelected,
            selectedTab : selectedtab,
            Interval : $("#maincombo").val(),
            resourceId : JResourceId,
            customer : servicefirst,
            service : servicesecond,
            date : $("#datepicker").val()
        };

    }else{
        reqdata = {
            hostName : host,
            metrictype : seriesName,
            resourcetype : selectedtab,
            ComboSelected : menuselelected,
            selectedTab : selectedtab,
            resourceId : JResourceId,
            Interval : $("#maincombo").val()
        };
    }
    if (tempSelectedtab == "Business View") {
        selectedtab = tempSelectedtab;
        tempSelectedtab = "";
    }
    chartHeading = menuselelected;
    summarywindow(reqdata);

}

/** Creating Line Chart */

function DrawLineChart(division1, menuselelected) {
    var index = 0;
    var scaleStart = 0;
    var scaleTemp = [];
    var actualMetricNames = [];
    var flagIndex = false;
    var seriesArrayColor = [ "#799999", "#0c242e", "#8cc7e0", "#174356",
    "#334356" ];
    if (selectedtab == "Customer View") {
        selectedtab = "Customer";
    }
    if (selectedtab == "Server")
        selectedtab = "server";
    if (selectedtab == "Business View") {
        tempSelectedtab = selectedtab;
        selectedtab = sub;
    }

    if (selectedtab == "server")
        selectedtab = "Server";
    if (tempSelectedtab == "Business View") {
        selectedtab = tempSelectedtab;
        tempSelectedtab = "";
    }
    var yaxisvalues = [];
    var temp = [];
    var szTemp = [];
    var metricUOMs = [];
    var slaTemp = [];
    var stepLength = 1;
    var linechartdataforip;

    if (selectedtab == "Customer") {
        /*if(menuselelected == "ActualvsExpectedEnergy"){
            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + "EnergyPerformance.json");
        }
        if((menuselelected == "PerformanceRatio") || (menuselelected == "EnergyPerRatedPower") || (menuselelected == "YieldHistory")){
		
            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + "PerformanceRatio.json");
        }
	if((menuselelected == "DerivedPerformanceRatio") || (menuselelected == "DerivedEnergyPerRatedPower")){
            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + "Inverter" + "PerformanceRatio.json");
        }*/

        //if ((fourthIteration) && (!(menuselelected.startsWith("Derived")))) {
        if (fourthIteration) {
            if(menuselelected == "Irradiation"){
                linetimestamps = [];
                linetimestamps[0] = [];
                linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                    + "Inverter"+menuselelected + ".json");
                for(var p = -1, len1 = linechartdataforip.length; ++p < len1;){
                    temp.push(linechartdataforip[p].IrradiationValue[0].cumm);
                    linetimestamps[0].push(linechartdataforip[p].timestamp);
                }
                yaxisvalues.push({
                    name : "Irradiation",
                    data : temp,
                    width : 1
                });
                seriesArrayColor = [ "#FFBF00"];
            }else{

                linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                    + menuselelected + "/" + servicethird
                    + "LineChartByTime.json");
                for ( var p = -1, len1 = linechartdataforip.length; ++p < len1;) {
                    if (linechartdataforip[p].ServerName == host) {
                        index = p;
                        flagIndex = true;
                        break;
                    }
                }
            }
        } else {
            for ( var p = -1, len1 = linechartdataforip.length; ++p < len1;) {
                if ((linechartdataforip[p].ServiceName == host) || (linechartdataforip[p].ResourceID == host)) {
                    index = p;
                    flagIndex = true;
                    break;
                }
            }
        }

    } else {

        linechartdataforip = srReadFiles1(customer + "/" + selectedtab + "/"
            + menuselelected + "/" + selectedtab + "LineChartByTime.json");
        for ( var p in linechartdataforip) {

            if (linechartdataforip[p].ServerName == host
                && linechartdataforip[p].ResourceID == JResourceId) {

                index = p;
                flagIndex = true;
                break;
            }
        }
    }
    if (linechartdataforip[index] != undefined && flagIndex) {
        linetimestamps = [];
        linetimestamps = linechartdataforip[index].TimeStamps;
        temp = linechartdataforip[index].ResourceValues;

        slaTemp = linechartdataforip[index].SlaValues;
        metricUOMs = linechartdataforip[index].MetricUOM;

        if (slaTemp == null) {
            slaTemp = linechartdataforip[index].ExpectedEnergy;
        }

        resourceName = linechartdataforip[index].ResourceNames;
        actualMetricNames = linechartdataforip[index].ActualMetricTypes;
        if (JSON.stringify(resourceName).indexOf("Availability") != -1) {
            availTimestamps = linechartdataforip[index].TimeStamps;
            inc = 0;
        } else{
            //added for y axis scale unit
            scaleTemp[0] = getArrayMin(temp);
            scaleTemp[1] = getArrayMin(slaTemp);
            scaleStart = getArrayMin(scaleTemp);

        }
        stepLength = parseInt(linetimestamps[0].length / 11);

        if (stepLength == 0)
            stepLength = 1;
        else if (stepLength == 1)
            stepLength = 2;
    }

    var criticalFlag = false;

    for ( var k in resourceName) {

        if (criticalMetricList.indexOf(resourceName[k] + ",") != -1
            && (resourceName[k].toLowerCase().indexOf('max') == -1 && resourceName[k]
                .toLowerCase().indexOf('peak') != 0)) {

            realMetricType = actualMetricNames[k];
            displayMetrictype = resourceName[k];
            yaxisvalues.push({
                name : resourceName[k] + " Threshold",
                data : slaTemp[k],
                width : 1
            }, {
                name : resourceName[k],
                data : temp[k],
                width : 1
            });
            //seriesArrayColor = [ "red", "white" ];
            //seriesArrayColor = [ "red", "#A7008F"];
            seriesArrayColor = [ "red", "#FFBF00"];
            if(menuselelected == "Irradiation"){
                seriesArrayColor = [ "#FFBF00"];
            }
            criticalFlag = true;
            break;
        }
    }
    if (flagIndex && !criticalFlag) {
        var m = 0;
        if (resourceName[m].toLowerCase().indexOf('max') != -1
            || resourceName[m].toLowerCase().indexOf('peak') != -1) {
            m = 1;
        }
        realMetricType = actualMetricNames[m];
        displayMetrictype = resourceName[m];
        if (menuselelected == "ActualvsExpectedEnergy"){
            yaxisvalues.push({
                name : resourceName[m],
                data : temp[m],
                width : 1
            }, {
                name : resourceName[m + 1],
                data : temp[m + 1],
                width : 1
            });
        }// else if(menuselelected.contains("PerformanceRatio")){
        else if(menuselelected.indexOf("PerformanceRatio") > -1){
            yaxisvalues.push({
                name : resourceName[m],
                data : temp[m],
                width : 1
            });
        } //else if(menuselelected.contains("EnergyPerRatedPower")){
        else if(menuselelected.indexOf("EnergyPerRatedPower") > -1){
            yaxisvalues.push({
                name : resourceName[m + 1],
                data : temp[m + 1],
                width : 1
            }); 
        } else if(menuselelected == "YieldHistory"){
            yaxisvalues.push({
                name : resourceName[m + 2],
                data : temp[m + 2],
                width : 1
            });
        }else {
            yaxisvalues.push({
                name : resourceName[m] + " Threshold",
                data : slaTemp[m],
                width : 1
            }, {
                name : resourceName[m],
                data : temp[m],
                width : 1
            });

        }
        //seriesArrayColor = [ "red", "white" ];
        //seriesArrayColor = [ "red", "#A7008F"];
        seriesArrayColor = [ "red", "#FFBF00"];

    }

    var headingName = "";
    if (realMetricType != undefined) {
        /*headingName = "Time Line " + realMetricType.charAt(0).toUpperCase()
        + realMetricType.slice(1);*/
        headingName = menuselelected;
        if (menuselelected == "ActualvsExpectedEnergy") {
            headingName = "Actual vs Expected Energy";
        }
        headingName = headingName;
    }
    if (division1.indexOf('2') > 0) {
        $("#" + chartheaddiv[1]).val(headingName+" " +metricUOMs);
        listOfMetricNames[1] = displayMetrictype;
    } else if (division1.indexOf('3') > 0) {
        $("#" + chartheaddiv[2]).val(headingName+" " +metricUOMs);
        listOfMetricNames[2] = displayMetrictype;
    } else if (division1.indexOf('4') > 0) {
        $("#" + chartheaddiv[3]).val(headingName+" " +metricUOMs);
        listOfMetricNames[3] = displayMetrictype;
    } else {
        $("#" + chartheaddiv[0]).val(headingName+" " +metricUOMs);
        listOfMetricNames[0] = displayMetrictype;
    }
    if (selectedtab == "Server")
        selectedtab = "server";
    if (selectedtab == "Business View") {
        tempSelectedtab = selectedtab;
        selectedtab = sub;
    }

    $(division1).click(
        function() {
            var headingName = $(this).parent().find('div table tr td + td')
            .find('input').val();

            if (division1.indexOf('2') > 0
                && headingName.indexOf("TimeLine") != -1) {
                lineSeriesClick(menuselelected, listOfMetricNames[1]);
            } else if (division1.indexOf('3') > 0
                && headingName.indexOf("TimeLine") != -1) {
                lineSeriesClick(menuselelected, listOfMetricNames[2]);
            } else if (division1.indexOf('4') > 0
                && headingName.indexOf("TimeLine") != -1) {
                lineSeriesClick(menuselelected, listOfMetricNames[3]);
            } else if (headingName.indexOf("TimeLine") != -1) {
                lineSeriesClick(menuselelected, listOfMetricNames[0]);
            }

        });

    pivotValue = "";
    if (JSON.stringify(temp) == "[]") {
        $('#loader').hide();
        DisplayNoDataImage(division1);
    } else if (JSON.stringify(resourceName).indexOf("Availability") != -1) {

        $(division1).kendoChart({
            theme : selectedTheme,
            legend : {
                position : "bottom",
                visible : false
            },
            seriesColors : [ "green", "red" ],
            seriesDefaults : {
                type : "scatterLine",
                markers : {
                    size : 4,
                    type : "triangle"
                }
            },
            series : [ {
                name : "AvailUp",
                data : temp
            }, {
                name : "AvailDown",
                data : slaTemp
            } ],
            xAxis : {
                max : parseInt(linetimestamps.length),
                majorUnit : 1,
                labels : {
                    template : "#= shortLabels(value) #",
                    rotation : 320
                }
            },
            yAxis : {
                max : 1,
                majorUnit : 1,
                labels : {
                    template : "#= shortYLabels(value) #",
                    rotation : 320
                }
            },
            tooltip : {
                visible : true,
                template : "#= showtooltip(value.x) #",
                color: "black"

            }
        });
    } else {
        $(division1).kendoChart({
            theme : selectedTheme,
            legend : {
                position : "bottom",
                visible : false
            },
            seriesDefaults : {
                type : "line",
                markers : {
                    size : 0
                }
            },
            series : yaxisvalues,
            seriesColors : seriesArrayColor,
            valueAxis : {
                //   min : "#= minYAxisValue() #",
                min : scaleStart,
                labels : {
                    template : "#=YLabelShortening(value) #",
                    step : 2
                },
                majorGridLines : {
                    visible : true
                }
            },
            //dataBound: onDataBoundFun(),
            seriesClick : function(e) {
                e.preventDefault();
                var reqdata;
                var tempSelectedtab = "";
                if (selectedtab == "Server") {
                    selectedtab = "server";
                }
                if (selectedtab == "Business View") {
                    tempSelectedtab = selectedtab;
                    selectedtab = sub;
                }
                if (selectedtab == "Customer View") {
                    if (firstIteration) {

                        tempSelectedtab = host;
                    }
                    if (secondIteration) {
                        tempSelectedtab = servicesecond;
                    }
                    if (thirdIteration) {
                        tempSelectedtab = servicethird;
                    }
                    if (fourthIteration) {

                        tempSelectedtab = servicethird;

                    }
                    reqdata = {
                        hostName : host,
                        metrictype : e.category,
                        resourcetype : tempSelectedtab,
                        ComboSelected : menuselelected,
                        selectedTab : selectedtab,
                        Interval : $("#maincombo").val(),
                        resourceId : JResourceId,
                        customer : servicefirst,
                        service : servicesecond,
                        date : $("#datepicker").val()
                    };

                } else {

                    reqdata = {
                        hostName : host,
                        metrictype : e.category,
                        resourcetype : selectedtab,
                        ComboSelected : menuselelected,
                        selectedTab : selectedtab,
                        Interval : $("#maincombo").val(),
                        resourceId : JResourceId
                    };
                }

                if (tempSelectedtab == "Business View") {
                    selectedtab = tempSelectedtab;
                    tempSelectedtab = "";
                }

                chartHeading = menuselelected;
                summarywindow(reqdata);

            },
            categoryAxis : {
                categories : linetimestamps[0],
                labels : {
                    template : "#= shortLabels(value)#",
                    rotation : 320,
                    step : stepLength
                }
            },
            tooltip : {
                visible : true,
                template : "#= series.name #= ${value}",
                color: "black"

            }
        });
    }
    $('#loader').hide();
    if (selectedtab == "Customer") {
        selectedtab = "Customer View";
    }
    if (selectedtab == "server")
        selectedtab = "Server";
    if (tempSelectedtab == "Business View") {
        selectedtab = tempSelectedtab;
        tempSelectedtab = "";
    }
}
function minYAxisValue(){
    alert("Hello...");
}
function YLabelShortening(value) {
    if (selectedtab == 'Network' && value > (100000 / 4)) {
        return (value / (1000000)).toFixed(1) + " M";
        if (tempSelectedtab == "Business View") {
            selectedtab = tempSelectedtab;
            tempSelectedtab = "";
        }
    } else if (selectedtab == 'JVM' && value > (104857 / 3)) {
        return (value / (1024 * 1024)).toFixed(1) + " Mb";
    } else
        return value;
}

function shortYLabels(value) {
    if (value)
        return 'Up';
    else
        return 'Down';
}

function showtooltip(value) {
    return availTimestamps[value];
}
/** Creating Column Chart */
function DrawColumnChart(division1, menuselelected) {

    var index = 0;
    var flagIndex = false;
    if (selectedtab == "Customer View") {
        selectedtab = "Customer";
    }
    if (selectedtab == "Server")
        selectedtab = "server";
    if (selectedtab == "Business View") {
        tempSelectedtab = "Business View";
        selectedtab = sub;
    }

    var metricChartTypeRelation1 = [];
    var common_ResourceMetricNames = [];
    JsonData1 = srReadFiles1("jsonconfigurations/" + selectedtab
        + "ComboSource.json");
    if (selectedtab == "Customer") {
        if (customerUrl != null) {
            JsonData1 = srReadFiles1("jsonconfigurations/" + customerUrl
                + "ComboSource.json");
        }
    }

    metricChartTypeRelation1 = JsonData1[0].items[0].items;

    for ( var p = -1, len = metricChartTypeRelation1.length; ++p < len;) {
        if (metricChartTypeRelation1[p].text.indexOf("Time Line") == -1
            && menuselelected == (metricChartTypeRelation1[p].text)
            .replace(/ /g, '')) {
            common_ResourceMetricNames = metricChartTypeRelation1[p].metricType;
            break;
        }
    }

    var linechartdataforip;
    var isDerived = 0;
    var stepLength = 1;
    var metricUOMString = [];

    if (selectedtab == "Customer") {
        linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
            + menuselelected + ".json");

        if (thirdIteration) {

            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + menuselelected + ".json");
        }

        /*if((menuselelected == "EnergyPerRatedPower") || (menuselelected == "PerformanceRatio") || (menuselelected == "YieldHistory")){
            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + "PerformanceRatio" + ".json");
            isDerived = 1;
        }
        if((menuselelected == "DerivedPerformanceRatio") || (menuselelected == "DerivedEnergyPerRatedPower")){
            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + "Inverter" + "PerformanceRatio.json");
            isDerived = 2;
        }*/
        if((menuselelected == "EnergyPerRatedPower") || (menuselelected == "PerformanceRatio") || (menuselelected == "YieldHistory") || (menuselelected == "EnergyTrends")){
            if(fourthIteration){
                linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                    + "Inverter" + "PerformanceRatio.json");
                isDerived = 2;
            }else{
                linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                    + "PerformanceRatio" + ".json");
                isDerived = 1;
            }
        }
        



        for ( var p = -1, len1 = linechartdataforip.length; ++p < len1;) {

            if(isDerived == 1){
                index = p;
                flagIndex = true;
                break;
            }else if(isDerived == 2){
                if (linechartdataforip[p].ResourceID == host) {
                    index = p;
                    flagIndex = true;
                    break;
                }
            }else  if (linechartdataforip[p].ServiceName == host) {
                index = p;
                flagIndex = true;
                break;
            }
        }

        if ((fourthIteration) && (isDerived == 0)) {

            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + menuselelected + "/" + servicethird + "MetricTypes.json");

            for ( var p = -1, len1 = linechartdataforip.length; ++p < len1;) {

                if (linechartdataforip[p].ServerName == host
                    && linechartdataforip[p].ResourceID == JResourceId) {

                    index = p;
                    flagIndex = true;
                    break;
                }
            }
        }

    } else {

        linechartdataforip = srReadFiles1(customer + "/" + selectedtab + "/"
            + menuselelected + "/" + selectedtab + "MetricTypes.json");
        for ( var p = -1, len1 = linechartdataforip.length; ++p < len1;) {
            if (linechartdataforip[p].ServerName == host
                && linechartdataforip[p].ResourceID == JResourceId) {

                index = p;
                flagIndex = true;
                break;
            }
        }
    }
    if (selectedtab == "server")
        selectedtab = "Server";
    if (tempSelectedtab == "Business View") {
        selectedtab = tempSelectedtab;
        tempSelectedtab = "";
    }

    if (JSON.stringify(linechartdataforip) == "[]" || !flagIndex)
        linechartdataforip = [ {
            ResourceNames : [],
            ResourceValues : [],
            MetricUOM : [],
            SlaValues : [],
            Health : []
        } ];
    common_ResourceMetricNames1 = linechartdataforip[index].ResourceNames;

    common_ResourceMetricValues1 = linechartdataforip[index].ResourceValues;

    common_ResourceMetricSlaValues1 = linechartdataforip[index].SlaValues;

    common_ResourceMetricHealthStatus = linechartdataforip[index].Health;

    metricUOMs = linechartdataforip[index].MetricUOM;


    common_ResourceMetricValues = [];
    common_ResourceMetricSlaValues = [];
    if (JSON.stringify(common_ResourceMetricNames1) != "[]"
        && common_ResourceMetricNames1 != undefined && isDerived == 0) {
        for ( var i = -1, len2 = common_ResourceMetricNames.length; ++i < len2;) {
            var mvalue = 0;
            var svalue = 0;
            for ( var j = -1, len3 = common_ResourceMetricNames1.length; ++j < len3;) {

                if (common_ResourceMetricNames[i] == common_ResourceMetricNames1[j]) {
                    mvalue = mvalue + common_ResourceMetricValues1[j];
                    svalue = svalue + common_ResourceMetricSlaValues1[j];
                }
            }
            common_ResourceMetricValues.push(mvalue);
            common_ResourceMetricSlaValues.push(svalue);
        }
    }

    if (menuselelected.indexOf('Alert') != -1) {
        common_ResourceMetricNames = linechartdataforip[index].ResourceNames;
        common_ResourceMetricValues = linechartdataforip[index].ResourceValues;
        common_ResourceMetricSlaValues = linechartdataforip[index].SlaValues;
    }
    var headingName = [];
    headingName = menuselelected;
    
    if((isDerived == 1) || (isDerived == 2)){
        for(var j=0; j<linechartdataforip[p].ResourceNames.length; j++){
            //menuselelected = menuselelected.replace("Derived","");
            if((menuselelected == "YieldHistory") || (menuselelected == "EnergyTrends")){
                menuselelected = "Yield";
            }
            if(menuselelected==linechartdataforip[p].ResourceNames[j]){
                common_ResourceMetricNames = linechartdataforip[p].TimeStamps[j];
                common_ResourceMetricValues = linechartdataforip[p].ResourceValues[j];
                break;
            }
        }
    }
    $(division1).empty();
    var xaxisvalues = new Array();
    var slavalues = new Array();
    var yaxisvalues = new Array();
    var heighest = 0.0;
    if (JSON.stringify(common_ResourceMetricNames) != "[]"
        && common_ResourceMetricNames != undefined) {
        xaxisvalues = common_ResourceMetricNames;
        if(isDerived==1 || isDerived==2){
            yaxisvalues = common_ResourceMetricValues;
            if(yaxisvalues.length >30 && yaxisvalues.length <=50){
                stepLength = 2;
            }else if(yaxisvalues.length > 50 && yaxisvalues.length <=100){
                stepLength = 3;
            }else if(yaxisvalues.length > 100){
                stepLength = 100;
            }
        }else{
            for ( var i = 0; i < common_ResourceMetricNames.length; i++) {
                var thresholdValue = parseFloat(common_ResourceMetricSlaValues[i]);
                if (menuselelected.indexOf('Alert') != -1) {
                    thresholdValue = parseFloat(common_ResourceMetricValues[i]) + 10;
                    if (common_ResourceMetricNames[i].match("HSTDWN"))
                        thresholdValue = 0;
                }
                if (parseFloat(common_ResourceMetricValues[i]) > thresholdValue) {
                    if (common_ResourceMetricNames[i].match("Charge")
                        || common_ResourceMetricNames[i].match("Availability")) {
                        slavalues.push(parseFloat(0));
                        yaxisvalues
                        .push(parseFloat(common_ResourceMetricValues[i]));
                        var temp = parseFloat(yaxisvalues[i]);
                        if (temp > heighest) {
                            heighest = temp;
                        }
                    } else {
                        slavalues[i] = parseFloat(common_ResourceMetricValues[i]
                            - thresholdValue);
                        yaxisvalues[i] = thresholdValue;
                        var temp = parseFloat(slavalues[i])
                        + parseFloat(yaxisvalues[i]);
                        if (temp > heighest) {
                            heighest = temp;
                        }
                    }
                } else if (parseFloat(common_ResourceMetricValues[i]) <= thresholdValue) {
                    if (common_ResourceMetricNames[i].match("Charge")
                        || common_ResourceMetricNames[i].match("Availability")
                        || common_ResourceMetricNames[i].match("HSTDWN")) {
                        slavalues.push(parseFloat(common_ResourceMetricValues[i]));
                        yaxisvalues.push(parseFloat(0));
                        var temp = parseInt(slavalues[i]);
                        if (temp > heighest) {
                            heighest = temp;
                        }
                    } else {
                        yaxisvalues[i] = parseFloat(common_ResourceMetricValues[i]);
                        slavalues[i] = parseFloat(0);
                        if (parseInt(common_ResourceMetricValues[i]) > heighest) {
                            heighest = parseFloat(common_ResourceMetricValues[i]);
                        }
                    }
                }
            }
        }
    }

    var maxValuewithstepArray;
    if ((heighest) <= 100 && (heighest) >= 1)
        maxValuewithstepArray = {
            max : 100,
            majorGridLines : {
                visible : true
            },
            majorUnit : 20
        };
    else
        maxValuewithstepArray = {
            labels : {
                template : "#= YLabelShortening(value)#",
                step : 2
            },
            majorGridLines : {
                visible : true
            }
        };

    if (selectedtab == "Server")
        selectedtab = "server";
    if (selectedtab == "Business View") {
        tempSelectedtab = selectedtab;
        selectedtab = sub;
    }

    if (JSON.stringify(yaxisvalues) == "[]") {

        DisplayNoDataImage(division1);
    } else
        $(division1).kendoChart({

            seriesClick : function(e) {
                e.preventDefault();
                var reqdata;
                var tempSelectedtab = "";
                if (selectedtab == "Server") {
                    selectedtab = "server";
                }
                if (selectedtab == "Business View") {
                    tempSelectedtab = selectedtab;
                    selectedtab = sub;
                }
                if (selectedtab == "Customer View") {

                    if (firstIteration) {

                        tempSelectedtab = host;
                    }
                    if (secondIteration) {
                        tempSelectedtab = servicesecond;
                    }
                    if (thirdIteration) {
                        tempSelectedtab = servicethird;
                    }
                    if (fourthIteration) {

                        tempSelectedtab = servicethird;

                    }
                    reqdata = {
                        hostName : host,
                        metrictype : e.category,
                        resourcetype : tempSelectedtab,
                        ComboSelected : menuselelected,
                        selectedTab : selectedtab,
                        Interval : $("#maincombo").val(),
                        resourceId : JResourceId,
                        customer : servicefirst,
                        service : servicesecond,
                        date : $("#datepicker").val()
                    };

                } else {

                    reqdata = {
                        hostName : host,
                        metrictype : e.category,
                        resourcetype : selectedtab,
                        ComboSelected : menuselelected,
                        selectedTab : selectedtab,
                        Interval : $("#maincombo").val(),
                        resourceId : JResourceId
                    };
                }

                if (tempSelectedtab == "Business View") {
                    selectedtab = tempSelectedtab;
                    tempSelectedtab = "";
                }
                chartHeading = menuselelected;
                summarywindow(reqdata);

            },
            theme : selectedTheme,
            legend : {
                position : "bottom",
                visible : false
            },
            seriesDefaults : {
                type : "column",
                stack : true
            },
            series : [ {
                name : "threshold",
                data : yaxisvalues
            }, {
                name : "crossedthreshold",
                data : slavalues
            } ],
            //seriesColors : [ "yellow", "red" ],
            //seriesColors : [ "white", "red" ],
            seriesColors : [ "#FFBF00","red"],
            valueAxis : maxValuewithstepArray,
            categoryAxis : {
                categories : xaxisvalues,
                labels : {
                    template : "#= shortLabels2(value) #",
                    //step : 20,
                    step : stepLength,
                    rotation : 320
                }
            },
            tooltip : {
                visible : true,
                template : " ${category}" + "=" + "${value}",
                color: "black"
            }
        });
    $('#loader').hide();
    if (selectedtab == "Customer") {
        selectedtab = "Customer View";
    }
    if (selectedtab == "server")
        selectedtab = "Server";
    if (tempSelectedtab == "Business View") {
        selectedtab = tempSelectedtab;
        tempSelectedtab = "";
    }
	
    if(headingName == "PerformanceRatio"){
        if(metricUOMs[0] == null){
            metricUOMs[0] = "";
        }
        if (division1.indexOf('2') > 0) {
            $("#" + chartheaddiv[1]).val(headingName+" " +metricUOMs[0]);
            listOfMetricNames[1] = displayMetrictype;
        } else if (division1.indexOf('3') > 0) {
            $("#" + chartheaddiv[2]).val(headingName+" " +metricUOMs[0]);
            listOfMetricNames[2] = displayMetrictype;
        } else if (division1.indexOf('4') > 0) {
            $("#" + chartheaddiv[3]).val(headingName+" " +metricUOMs[0]);
            listOfMetricNames[3] = displayMetrictype; 
        }
        else {
            $("#" + chartheaddiv[0]).val(headingName+" " +metricUOMs[0]);
            listOfMetricNames[0] = displayMetrictype;
        }
    }

	
    else if(headingName == "EnergyPerRatedPower") {
        if(metricUOMs[1] == null){
            metricUOMs[1] = "";              
        }

        if (division1.indexOf('2') > 0) {
            $("#" + chartheaddiv[1]).val(headingName+" " +metricUOMs[1]);
            listOfMetricNames[1] = displayMetrictype;
        } else if (division1.indexOf('3') > 0) {
            $("#" + chartheaddiv[2]).val(headingName+" " +metricUOMs[1]);
            listOfMetricNames[2] = displayMetrictype;
        } else if (division1.indexOf('4') > 0) {
            $("#" + chartheaddiv[3]).val(headingName+" " +metricUOMs[1]);
            listOfMetricNames[3] = displayMetrictype;
        } else {
            $("#" + chartheaddiv[0]).val(headingName+" " +metricUOMs[1]);
            listOfMetricNames[0] = displayMetrictype;
        }
    }



    else if((headingName == "YieldHistory") || (headingName == "EnergyTrends")) {
        if(metricUOMs[2] == null){
            metricUOMs[2] = "";              
        }
        if (division1.indexOf('2') > 0) {
            $("#" + chartheaddiv[1]).val(headingName+" " +metricUOMs[2]);
            listOfMetricNames[1] = displayMetrictype;
        } else if (division1.indexOf('3') > 0) {
            $("#" + chartheaddiv[2]).val(headingName+" " +metricUOMs[2]);
            listOfMetricNames[2] = displayMetrictype;
        } else if (division1.indexOf('4') > 0) {
            $("#" + chartheaddiv[3]).val(headingName+" " +metricUOMs[2]);
            listOfMetricNames[3] = displayMetrictype;
        } else {
            $("#" + chartheaddiv[0]).val(headingName+" " +metricUOMs[2]);
            listOfMetricNames[0] = displayMetrictype;
        }
    }

/*else {
    if (division1.indexOf('2') > 0) {
        $("#" + chartheaddiv[1]).val(headingName+"("+metricUOMs[3]+")");
        listOfMetricNames[1] = displayMetrictype;
    } else if (division1.indexOf('3') > 0) {
        $("#" + chartheaddiv[2]).val(headingName+"("+metricUOMs[3]+")");
        listOfMetricNames[2] = displayMetrictype; 
    } else if (division1.indexOf('4') > 0) {
        $("#" + chartheaddiv[3]).val(headingName+"("+metricUOMs[3]+")");
        listOfMetricNames[3] = displayMetrictype;
    } else {
        $("#" + chartheaddiv[0]).val(headingName+"("+metricUOMs[3]+")");
        listOfMetricNames[0] = displayMetrictype;
    }
}   */
}

/** Creating Column Chart */
function DrawGroupedColumnChart(division1, menuselelected) {

    if (selectedtab == "Customer View") {
        selectedtab = "Customer";
    }
    var flagIndex = true;
    //alert("menuselelected::"+menuselelected);
    JsonData1 = srReadFiles1("jsonconfigurations/" + selectedtab
        + "ComboSource.json");

    metricChartTypeRelation1 = JsonData1[0].items[0].items;
    var linechartdataforip;
    if (selectedtab == "Customer") {
        if((menuselelected == "PerformanceRatioComparison") || (menuselelected == "YieldComparison")){
            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + "PerformanceRatioComparison" + ".json");
        }else if((menuselelected == "EnergyPerRatedPower") || (menuselelected == "PerformanceRatio") || (menuselelected == "ActualvsTargetEnergy")){
            linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
                + "PerformanceRatio" + ".json");
        }
    } else {
        linechartdataforip = srReadFiles1(customer + "/" + hostUrl + "/"
            + "EnergyPerformance" + ".json");
    }

    var common_ResourceMetricNames = [];
    var common_ResourceMetricValues = [];
    var common_ResourceMetricValues1 = [];

    if (JSON.stringify(linechartdataforip) == "[]" || !flagIndex) {
        linechartdataforip = [ {
            ResourceNames : [],
            ResourceValues : [],
            MetricUOM : [],
            SlaValues : [],
            Health : []
        } ];
    } else {
        if (menuselelected.indexOf("PerformanceRatioComparison") != -1) {
            for ( var i = 0; i < linechartdataforip.length; i++) {
                common_ResourceMetricNames
                .push(linechartdataforip[i].ResourceID);
                common_ResourceMetricValues
                .push(linechartdataforip[i].PerformanceRatio);
            //common_ResourceMetricValues1
            //.push(linechartdataforip[i].EnergyPerRatedPower);
            }

        } else if (menuselelected.indexOf("ActualvsTargetEnergy") != -1) {

            for ( var i = 0; i < linechartdataforip.length; i++) {
                common_ResourceMetricNames
                .push(linechartdataforip[i].ServiceName);
                common_ResourceMetricValues
                .push(linechartdataforip[i].ActualEnergy);
                common_ResourceMetricValues1
                .push(linechartdataforip[i].TargetEnergy);
            }
        }else if (menuselelected.indexOf("PerformanceRatio") != -1) {
            /*	var szIndx;
		if(secondIteration || fourthIteration){
			alert("PR at inst/dev level");
			var resNames = linechartdataforip[0].ResourceNames;
			for ( var i = 0; i < resNames.length; i++) {
				if(resNames[i].indexOf("PerformanceRatio") != -1){
					alert("Found PR at index:"+i);
					szIndx = i;
					break;
				}
			}
			common_ResourceMetricNames
                .push(linechartdataforip[0].TimeStamps[szIndx]);
		//.push("timestamp");
			common_ResourceMetricValues
                .push(linechartdataforip[0].ResourceValues[szIndx]);
		}else{*/
            for ( var i = 0; i < linechartdataforip.length; i++) {
                common_ResourceMetricNames
                .push(linechartdataforip[i].ServiceName);
                common_ResourceMetricValues
                .push(linechartdataforip[i].PerformanceRatio);
            }
        //}

        } else if (menuselelected.indexOf("EnergyPerRatedPower") != -1) {
            for ( var i = 0; i < linechartdataforip.length; i++) {
                common_ResourceMetricNames
                .push(linechartdataforip[i].ServiceName);
                common_ResourceMetricValues
                .push(linechartdataforip[i].EnergyPerRatedPower);
            }

        } else if (menuselelected.indexOf("YieldComparison") != -1) {
            for ( var i = 0; i < linechartdataforip.length; i++) {
                common_ResourceMetricNames
                .push(linechartdataforip[i].ResourceID);
                common_ResourceMetricValues
                .push(linechartdataforip[i].Yield);
            }

        }

    }

    $(division1).empty();
    var xaxisvalues = new Array();
    var actual_Energy_Series = new Array();
    var expected_Energy_Series = new Array();
    var stack = false;
    var groupedtemplate = "";
    if (JSON.stringify(common_ResourceMetricNames) != "[]"
        && common_ResourceMetricNames != undefined) {
        xaxisvalues = common_ResourceMetricNames;
        if((menuselelected.indexOf("ActualvsTargetEnergy") != -1) || (menuselelected.indexOf("YieldComparison") != -1)){
            for ( var i = 0; i < common_ResourceMetricNames.length; i++) {
                actual_Energy_Series[i] = parseFloat(common_ResourceMetricValues[i]);
                expected_Energy_Series[i] = parseFloat(common_ResourceMetricValues1[i]);
            }
            stack = false;
            groupedtemplate = "#= series.name # = #= value #";
        }else if((menuselelected.indexOf("PerformanceRatioComparison") != -1)){
            for ( var i = 0; i < common_ResourceMetricNames.length; i++) {
                actual_Energy_Series[i] = parseFloat(common_ResourceMetricValues[i]);
            //expected_Energy_Series[i] = parseFloat(common_ResourceMetricValues1[i]);
            }
            stack = false;
            groupedtemplate = "#= series.name # = #= value #";
        }else if ( (menuselelected.indexOf("Efficiency") != -1) || (menuselelected.indexOf("PerformanceRatio") != -1) || (menuselelected.indexOf("EnergyPerRatedPower") != -1)) {

            for ( var i = 0; i < common_ResourceMetricNames.length; i++) {
                actual_Energy_Series[i] = parseFloat(common_ResourceMetricValues[i]);

            }
            stack = true;
            groupedtemplate = " ${category}" + " = " + "${value}";
        } else {
            for ( var i = 0; i < common_ResourceMetricNames.length; i++) {
                actual_Energy_Series[i] = parseFloat(common_ResourceMetricValues[i]);
                expected_Energy_Series[i] = parseFloat(common_ResourceMetricValues1[i]);
            }
            stack = false;
            groupedtemplate = "#= series.name # = #= value #";
        }
    }
    if (JSON.stringify(actual_Energy_Series) == "[]") {
        $('#loader').hide();
        DisplayNoDataImage(division1);
    } else {
        $(division1).kendoChart({
            theme : selectedTheme,
            seriesClick : function(e) {
                e.preventDefault();
                var reqdata;
                var tempSelectedtab = "";
                if (selectedtab == "Server") {
                    selectedtab = "server";
                }
                if (selectedtab == "Business View") {
                    tempSelectedtab = selectedtab;
                    selectedtab = sub;
                }
                if (selectedtab == "Customer View") {

                    if (firstIteration) {

                        tempSelectedtab = host;
                    }
                    if (secondIteration) {
                        tempSelectedtab = servicesecond;
                    }
                    if (thirdIteration) {
                        tempSelectedtab = servicethird;
                    }
                    if (fourthIteration) {

                        tempSelectedtab = servicethird;

                    }
                    reqdata = {
                        hostName : host,
                        metrictype : e.category,
                        resourcetype : tempSelectedtab,
                        ComboSelected : menuselelected,
                        selectedTab : selectedtab,
                        Interval : $("#maincombo").val(),
                        resourceId : JResourceId,
                        customer : servicefirst,
                        service : servicesecond,
                        date : $("#datepicker").val()
                    };

                } else {

                    reqdata = {
                        hostName : host,
                        metrictype : e.category,
                        resourcetype : selectedtab,
                        ComboSelected : menuselelected,
                        selectedTab : selectedtab,
                        Interval : $("#maincombo").val(),
                        resourceId : JResourceId
                    };
                }

                chartHeading = menuselelected;
                summarywindow(reqdata);

            },
            legend : {
                position : "bottom",
                visible : false
            },
            seriesDefaults : {
                type : "column",
                stack : stack
            },

            series : [ {
                data : actual_Energy_Series,
                name : "ActualEnergy",
                spacing : 0
            }, {
                data : expected_Energy_Series,
                name : "ExpectedEnergy"
            } ],
            valueAxis : {

                line : {
                    visible : false
                },
                axisCrossingValue : 0
            },
            categoryAxis : {
                categories : xaxisvalues,
                labels : {
                    rotation : 320
                }
            },
            tooltip : {
                visible : true,
                template : groupedtemplate,
                color: "black"

            }

        });
    }

    $('#loader').hide();
    if (selectedtab == "Customer") {
        selected = "Customer View";
    }
}

function DrawLinearGauge(division, menuselelected) {
    var xaxisvalues = [];
    var slavalues = [];
    var yaxisvalues = [];
    $(division).empty();
    var index = 0;
    var flagIndex = false;
    $(division).css('background',
        'url(lib/Default/linear-gauge-container.png) no-repeat ');
    $(division).css('backgroundPosition', 'center');

    if (selectedtab == "Server")
        selectedtab = "server";
    if (selectedtab == "Business View") {
        tempSelectedtab = selectedtab;
        selectedtab = sub;
    }
    var linechartdataforip = srReadFiles1(customer + "/" + selectedtab + "/"
        + menuselelected + "/" + selectedtab + "MetricTypes.json");
    if (selectedtab == "server")
        selectedtab = "Server";
    if (tempSelectedtab == "Business View") {
        selectedtab = tempSelectedtab;
        tempSelectedtab = "";
    }
    for ( var p = -1, len = linechartdataforip.length; ++p < len;) {
        if (linechartdataforip[p].ServerName == host
            && linechartdataforip[p].ResourceID == JResourceId) {
            index = p;
            flagIndex = true;
            break;
        }
    }
    /*
	 * if(selectedtab=="Server") selectedtab="server";
	 */
    if (linechartdataforip[index] != undefined && flagIndex) {
        xaxisvalues = linechartdataforip[index].ResourceNames;
        slavalues.push(linechartdataforip[index].SlaValues);
        yaxisvalues.push(linechartdataforip[index].ResourceValues);
    }
    if (JSON.stringify(yaxisvalues) == "[]") {
        $('#loader').hide();
        DisplayNoDataImage(division);
    } else
        $(division).kendoLinearGauge({
            theme : selectedTheme,
            background : "",
            pointer : {
                value : yaxisvalues[0],
                shape : "arrow",
                color : "yellow"
            },
            chartArea : {
                border : {
                    color : "",
                    dashType : "solid",
                    width : 0
                }
            },
            scale : {
                majorUnit : 20,
                minorUnit : 2,
                min : 0,
                max : 100,
                vertical : true,
                ranges : [ {
                    from : 0,
                    to : slavalues[0],
                    color : "green"
                }, {
                    from : slavalues[0],
                    to : 100,
                    color : "red"
                } ]
            }
        });
    $('#loader').hide();
}

/*function XLSReportWindow() {
	$
			.window({
				title : "XLS Reports",
				content : $("#DownloadXLSFiles"),
				width : 400, // window width
				height : 320,
				showModal : true,
				modalOpacity : 0.5,
				showFooter : false,
				showRoundCorner : true,
				minimizable : false,
				maximizable : false,
				headerClass : "my_header",
				bookmarkable : false,
				scrollable : false,
				draggable : false,
				resizable : false,
				onShow : function(wnd) {

					$
							.ajax({
								type : "POST",
								data : {
								// sessionid:sessionid
								},
								url : "/SA-DeskUI/Load_XLSReports.action",

								success : function(submsg) {
									var DIVString = "<ul type='none' id='xlsattach'>";

									var checkfilessub = $(submsg).find("name");
									var checkfilessublength = checkfilessub.length;
									for ( var k = 0; k < checkfilessublength; k++) {
										DIVString = DIVString
												+ "<li><img src='js/images/rsz_3move_waiting_down_alternative.png' onclick='DownloadthisXLSfile(\""
												+ $(checkfilessub[k]).text()
												+ "\")' alt='-'/><a href=\"javascript:DownloadthisXLSfile(\'"
												+ $(checkfilessub[k]).text()
												+ "\')\"> "
												+ $(checkfilessub[k]).text()
												+ "</a></li>";
									}
									DIVString = DIVString + "</ul>";
									wnd.getContainer().find(".FilesLinks")
												.html(
													"<div>" + DIVString
															+ "</div>");
								}
							});
				},
				onClose : function(wnd) {
				}
			});
}*/

function XLSReportWindow() {                                //modified till window
    var window = $("#window"),
    undo = $("#undo")
    .bind("click", function() {
        window.data("kendoWindow").open();
        undo.hide();
    });

    var onClose = function() {
        undo.show();
    };

    $.window({                                   //modified     
        title: "XLS Reports",
        content: $("#DownloadXLSFiles"),
        width: 500, // window width
        height: 280,
        showModal: true,
        modalOpacity: 0.5,
        showFooter: false,
        showRoundCorner: true,
        minimizable: false,
        maximizable: false,
        headerClass: "my_header",
        bookmarkable: false,
        scrollable: false,
        draggable: false,
        resizable: false,
        onShow: function(wnd) {

            $   
            .ajax({
                type: "POST",
                data: {
                // sessionid:sessionid
                },
                url: "/SA-DeskUI/Load_XLSReports.action",
                success: function(submsg) {
                    var DIVString = "<ul type='none' id='xlsattach' style=\"background-color: #1b141a\">";

                    var checkfilessub = $(submsg).find("name");
                    var checkfilessublength = checkfilessub.length;
                    for (var k = 0; k < checkfilessublength; k++) {
                        DIVString = DIVString
                        + "<li><img src='js/images/rsz_3move_waiting_down_alternative.png' onclick='DownloadthisXLSfile(\""
                        + $(checkfilessub[k]).text()
                        + "\")' alt='-'/><a href=\"javascript:DownloadthisXLSfile(\'"
                        + $(checkfilessub[k]).text()
                        + "\') \" style=\"color:white;background-color:#1b141a\"> "
                        + $(checkfilessub[k]).text()
                        + "</a></li>";
                    }
                    DIVString = DIVString + "</ul>";
                    wnd.getContainer().find(".FilesLinks")
                    .html(//modified
                        "<div style=\"background-color:#1b141a;\">" + DIVString
                        + "</div>");
                }
            });
        },
        close: onClose
    });
}

function DownloadthisXLSfile(filename) {
    window.location.href = "/SA-DeskUI/Donload_XLSfile.action?filename="
    + filename;
}

function closewnd() {
    $.window.closeAll();
}

function CustomerClearAll() {
    $(combodiv[10]).empty();
    $(combodiv[13]).empty();
    $(combodiv[11]).empty();
    $(combodiv[12]).empty();
    $(combodiv[0]).html('');
    $(combodiv[1]).html('');
    $(combodiv[2]).html('');
    $(combodiv[3]).html('');
    var header = "";
    $("#" + chartheaddiv[0]).val(header);
    $("#" + chartheaddiv[1]).val(header);
    $("#" + chartheaddiv[2]).val(header);
    $("#" + chartheaddiv[3]).val(header);

}

function getEstatus(e) {

    if (e != undefined) {
        return true;
    } else {
        return false;
    }
}
function getUserAuthenticity(userName) {

    var user = srReadFiles1(customerID + "/userAuthentication.json");

    for ( var i = 0; i < user.length; i++) {
        if (user[i].UserName == userName) {

            p = i;
            break;
        }
    }
    var userDetails = [];
    userDetails.push(user[p].Role);
    userDetails.push(user[p].AccessLevel);
    userDetails.push(user[p].ServicesFirst);
    return userDetails;

}

var matched, browser;

jQuery.uaMatch = function( ua ) {
    ua = ua.toLowerCase();

    var match = /(chrome)[ \/]([\w.]+)/.exec( ua ) ||
    /(webkit)[ \/]([\w.]+)/.exec( ua ) ||
    /(opera)(?:.*version|)[ \/]([\w.]+)/.exec( ua ) ||
    /(msie) ([\w.]+)/.exec( ua ) ||
    ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec( ua ) ||
    [];

    return {
        browser: match[ 1 ] || "",
        version: match[ 2 ] || "0"
    };
};

matched = jQuery.uaMatch( navigator.userAgent );
browser = {};

if ( matched.browser ) {
    browser[ matched.browser ] = true;
    browser.version = matched.version;
}

// Chrome is Webkit, but Webkit is also Safari.
if ( browser.chrome ) {
    browser.webkit = true;
} else if ( browser.webkit ) {
    browser.safari = true;
}

jQuery.browser = browser;

function disableTimeOut(){
    //alert('Data loaded!!!');
    setTimeout(function(){
        kendo.ui.progress($("#chart"), true);
    });
}

function onDataBoundFun(){
    alert("Loading this graph may take  a couple of mins. Click OK to proceed, cancel to stop loading");
    disableTimeOut();
}

function getArrayMin(datapoints) {
    var par = [];
    var temp = [];
    var yyy;
    if(datapoints == undefined){
        return 0;
    }else{
        for (var i = 0; i < datapoints.length; i++) {
            yyy = datapoints[i];
            if((JSON.stringify(yyy)).indexOf("null") != -1){ 
                continue;
            }
            var szTemp = JSON.parse("[" + datapoints[i] + "]");
            temp = szTemp;//.split(",");
            for (var j = 0; j < temp.length; j++) {
                if (!isNaN(temp[j])) {
                    par.push(temp[j]);
                }
            }
        }
        return Math.min.apply(Math,par);
    }
}


function shortLabels2(labelString) {
    if (labelString.toString().indexOf("/") != -1) {
        labelString = labelString.substring(labelString.indexOf("/") + 1, labelString.length);
    }
    return labelString;
}

