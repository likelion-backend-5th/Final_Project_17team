(function ($) {
    // Define a Vue instance for search results
    var search_result = new Vue({
        el: '#search-result',
        data: {
            search_result: []
        },
        methods: {
            sortResults: function (sortBy) {
                $.get(`/named?category=성시경 먹을텐데&sortBy=${sortBy}&page=0&limit=300`, function (response) {
                    search_result.search_result = response.content;

                });
            }
        }
    });

    // Function to handle the search button click
    $("#searchButton1").click(function () {
        $.get(`/named?category=성시경 먹을텐데&page=0&limit=300`, function (response) {
            // Update the search_result data property with the search results
            search_result.search_result = response.content;

            // Get the icon URL based on your logic (e.g., from a variable)
            var iconUrl = '/markerImages/성시경마커.png';

            // Call initializeMap with the icon URL
            initializeMap(iconUrl);

            $("#sorting-buttons").show();
        });
    });


    // Initialize the map when the Naver Map library is ready
    naver.maps.onJSContentLoaded = function () {
        var map = new naver.maps.Map('map', {
            center: new naver.maps.LatLng(37.3595704, 127.105399),
            zoom: 7
        });
    };

    function initializeMap(iconUrl) {
        // Create a map object centered at the average position of markers
        var map = new naver.maps.Map('map', {
            center: getMostFrequentMarkerPosition(),
            zoom: 11
        });

        var markers = [];

        // Function to create a marker and infowindow for a result
        function createMarker(result) {
            var markerOptions = {
                position: new naver.maps.LatLng(result.mapY, result.mapX),
                map: map,
                title: result.name,
            };

            // Check if iconUrl is provided, if so, set the custom icon URL
            if (iconUrl) {
                markerOptions.icon = {
                    url: iconUrl,
                    size: new naver.maps.Size(25, 25), // 마커 이미지의 크기를 조절하세요.
                    origin: new naver.maps.Point(657, 272), // 이미지의 원점을 설정합니다. 가지고 있는 이미지 1440 x 1440
                    anchor: new naver.maps.Point(16, 32) // 이미지의 앵커 지점을 설정합니다
                };
            }

            var marker = new naver.maps.Marker(markerOptions);

            markers.push(marker);

            var contentString = `
                <div class="iw_inner">
                    <p><b>${result.name}</b><br />
                    <b>주소: </b>${result.address}<br />
                    </p>
                </div>`;

            var infowindow = new naver.maps.InfoWindow({
                content: contentString,
                maxWidth: 160,
                backgroundColor: "#eee",
                borderColor: "#2db400",
                borderWidth: 5,
                anchorSize: new naver.maps.Size(30, 30),
                anchorSkew: true,
                anchorColor: "#eee",
                pixelOffset: new naver.maps.Point(20, -20)
            });

            naver.maps.Event.addListener(marker, "click", function () {
                if (infowindow.getMap()) {
                    infowindow.close();
                } else {
                    infowindow.open(map, marker);
                }
            });
        }
        // Loop through each search result and create a marker and infowindow
        for (var i = 0; i < search_result.search_result.length; i++) {
            createMarker(search_result.search_result[i]);
        }

    }

    function getMostFrequentMarkerPosition() {
        var searchResults = search_result.search_result;

        // Step 1: 결과를 10개씩 묶기
        var groupedResults = [];
        for (var i = 0; i < searchResults.length; i += 10) {
            var group = searchResults.slice(i, i + 10);
            groupedResults.push(group);
        }

        // Step 2: 각 그룹의 중심점 계산
        var groupCenters = [];
        groupedResults.forEach(function (group) {
            var sumLat = 0;
            var sumLng = 0;
            group.forEach(function (result) {
                sumLat += result.mapY;
                sumLng += result.mapX;
            });
            var centerLat = sumLat / group.length;
            var centerLng = sumLng / group.length;
            groupCenters.push({ lat: centerLat, lng: centerLng });
        });

        // Step 3: 이상치 제거
        var validCenters = removeOutliers(groupCenters);

        // Step 4: 중심점 간의 거리 계산하여 밀집되어 있는 좌표 찾기
        var mostDensePosition = findMostDensePosition(validCenters);

        return mostDensePosition;
    }

// 이상치를 제거하는 함수 (예: 1.5 * IQR 범위로 이상치를 정의)
    function removeOutliers(data) {
        var values = data.map(function (point) {
            return point.lat;
        });
        values.sort(function (a, b) {
            return a - b;
        });

        var q1 = values[Math.floor((values.length / 4))];
        var q3 = values[Math.ceil((values.length * 3 / 4))];
        var iqr = q3 - q1;
        var lowerBound = q1 - 1.5 * iqr;
        var upperBound = q3 + 1.5 * iqr;

        return data.filter(function (point) {
            return point.lat >= lowerBound && point.lat <= upperBound;
        });
    }

// 중심점 간의 거리를 계산하여 가장 밀집된 좌표 찾기
    function findMostDensePosition(centers) {
        var mostDensePosition = null;
        var highestDensity = 0;

        for (var i = 0; i < centers.length; i++) {
            var density = 0;
            for (var j = 0; j < centers.length; j++) {
                if (i !== j) {
                    var distance = calculateDistance(centers[i], centers[j]);
                    density += 1 / distance; // The shorter the distance, the higher the density
                }
            }
            if (density > highestDensity) {
                highestDensity = density;
                mostDensePosition = centers[i];
            }
        }

        return mostDensePosition;
    }

   // 두 지점 사이의 거리를 킬로미터 단위로 반환
    function calculateDistance(point1, point2) {
        var lat1 = point1.lat;
        var lng1 = point1.lng;
        var lat2 = point2.lat;
        var lng2 = point2.lng;

        // Convert latitude and longitude from degrees to radians
        var radLat1 = (Math.PI * lat1) / 180;
        var radLng1 = (Math.PI * lng1) / 180;
        var radLat2 = (Math.PI * lat2) / 180;
        var radLng2 = (Math.PI * lng2) / 180;

        // Earth's radius in kilometers
        var earthRadius = 6371; // You can also use 3958.8 for miles

        // Haversine formula
        var dLat = radLat2 - radLat1;
        var dLng = radLng2 - radLng1;
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(radLat1) * Math.cos(radLat2) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var distance = earthRadius * c;

        return distance;
    }

    $(document).ready(function () {
        console.log("init");

        // "지역별 맛집 검색" 링크 클릭 이벤트 처리
        $("#localSectionLink").click(function (event) {
            event.preventDefault(); // 기본 링크 동작을 막습니다.

            // 이동할 URL 설정
            var newURL = "http://localhost:8080/matprint/search";

            // 페이지를 새 URL로 이동합
            window.location.href = newURL;
        });
    });

    // Event listeners for sorting buttons
    $("#nameSortButton").click(function () {
        search_result.sortResults("이름");
    });

    $("#reviewSortButton").click(function () {
        search_result.sortResults("리뷰");
    });

    $("#ratingSortButton").click(function () {
        search_result.sortResults("평점");
    });

})(jQuery);