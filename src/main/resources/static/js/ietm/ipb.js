let canvas, ctx, img;
//let infos = [];
let systemWidth = 0;
let systemHeight = 0;
let blinkInterval;
let ipbCurrentPage = 1;

/* 테이블 열 너비 조정 */
function applyColumnWidths(widthMap) {
    const parsedData = JSON.parse(widthMap);

    for (const [key, value] of Object.entries(parsedData)) {
        const th = document.getElementById(key);
        if (th) {
            const widthValue = value === "auto" ? "auto" : value + "%";
            th.style.width = widthValue;

            document.querySelectorAll(`td[data-col="${key}"]`).forEach(td => {
                td.style.width = widthValue;
            });
        }
    }
}


/* 이미지 번호 누르면 해당 row active 표시 */
function rowActive(circle, circleInfo) {

    // 이전에 변경된 tr 스타일 초기화
    // tr 태그들에서 이전 스타일 리셋
    const allRows = document.querySelectorAll('tr');
    allRows.forEach(row => {
        row.style.backgroundColor = '';
        const td = row.querySelectorAll('td');
        td.forEach(td => {
            td.style.fontWeight = '';
        });
    });

    // 아직 타입 분류가 안돼서 같은 번호 행이 여러개 나와 일단 일치하는 모든 행의 배경색 변경하는 거로 진행
    const rows = document.querySelectorAll(`tr[name='part_${circleInfo.instancename}']`);
    rows.forEach(row => {
        row.style.backgroundColor = '#cfe2ff';

        // 안에 있는 td 스타일 변경
        const td = row.querySelectorAll('td');
        td.forEach(td => {
            td.style.fontWeight = 'bold';
        });

        // 해당 row 에 포커스
        row.setAttribute('tabindex', '-1');
        row.focus();
    });
}
/* 원 그리기 - imageDataJson --> 현재는 영역 확인을 위해 이미지에 불투명하게 표시되도록 해놨지만 추후에 투명으로 바꿀 예정  */
function drawCircles(imageDataJson) {

    const pngDivArea = document.querySelectorAll(".ipb-image-div:not(.d-none)");
    pngDivArea.forEach((div) => {
        const divId = div.id;
        const systemInfo = imageDataJson[divId];
        if (!systemInfo) return;

        const infos = systemInfo.infos;

        const circleCanvas = div.querySelector("#ipb-rowactive-canvas");
        const img = div.querySelector("#ipb-image");
        if (!circleCanvas || !img) return;

        let circles = [];
        let activeCircle = null;
        let ctx = circleCanvas.getContext("2d");

        const drawAllCircles = () => {
            ctx.clearRect(0, 0, circleCanvas.width, circleCanvas.height);
            circles.forEach(circle => {
                const { x, y, radius } = circle;
                ctx.beginPath();
                ctx.arc(x, y, radius, 0, 2 * Math.PI);
                if (activeCircle === circle) {
                    ctx.fillStyle = 'rgba(0, 0, 255, 0.3)';
                    ctx.fill();
                } else {
                    ctx.lineWidth = 1;
                    ctx.strokeStyle = 'rgba(0, 0, 0, 0.3)';
                    ctx.stroke();
                }
                ctx.closePath();
            });
        };

        const getCanvasCoords = (event) => {
            const rect = circleCanvas.getBoundingClientRect();
            const scaleX = circleCanvas.width / rect.width;
            const scaleY = circleCanvas.height / rect.height;
            return {
                x: (event.clientX - rect.left) * scaleX,
                y: (event.clientY - rect.top) * scaleY
            };
        };

        const handleClick = (event) => {
            const { x: clickX, y: clickY } = getCanvasCoords(event);
            circles.forEach((circle, index) => {
                const { x, y, radius } = circle;
                const distance = Math.hypot(clickX - x, clickY - y);
                if (distance <= radius && activeCircle !== circle) {
                    activeCircle = circle;
                    rowActive(circle, infos[index]);
                    drawAllCircles();
                }
            });
        };

        const handleMouseMove = (event) => {
            const { x: mouseX, y: mouseY } = getCanvasCoords(event);
            const hovering = circles.some(({ x, y, radius }) => {
                const distance = Math.hypot(mouseX - x, mouseY - y);
                return distance <= radius;
            });
            circleCanvas.style.cursor = hovering ? 'pointer' : 'default';
        };

        const resizeAndRedraw = () => {
            circleCanvas.width = img.clientWidth;
            circleCanvas.height = img.clientHeight;

            systemWidth = parseInt(systemInfo.width);
            systemHeight = parseInt(systemInfo.height);

            const scaleX = circleCanvas.width / systemWidth;
            const scaleY = circleCanvas.height / systemHeight;

            circles = infos.map(info => {
                const x1 = parseInt(info.x1);
                const y1 = parseInt(info.y1);
                const x2 = parseInt(info.x2);
                const y2 = parseInt(info.y2);
                if ([x1, y1, x2, y2].some(isNaN)) return null;

                const centerX = ((x1 + x2) / 2) * scaleX;
                const centerY = ((y1 + y2) / 2) * scaleY;

                return {
                    x: centerX + 1, // ▶ 오른쪽으로 1px만 이동
                    y: centerY,
                    radius: Math.abs(x2 - x1) * scaleX * 0.5
                };
            }).filter(Boolean);

            drawAllCircles();
        };

        img.onload = () => {
            resizeAndRedraw();
            circleCanvas.addEventListener("click", handleClick);
            circleCanvas.addEventListener("mousemove", handleMouseMove);
        };

        if (img.complete) img.onload();

        window.addEventListener("resize", () => {
            if (img.complete) resizeAndRedraw();
        });
    });
}

/* 핫스팟 - imageDataJson */
function updateHotSpotLink(imageDataJson) {
    const parsedData = JSON.parse(imageDataJson);

    const keys = Object.keys(parsedData);

    keys.forEach((key) => {

        const data = parsedData[key];
        if (data) {
            systemWidth = data.width;
            systemHeight = data.height;

            const infos = data.infos;
            if (Array.isArray(infos)) {
                infos.forEach((info, idx) => {
                    // 품목번호 a 링크에 hotspot 기능 적용
                    document.querySelectorAll("a[id^='hotSpot_'").forEach((a) => {
                        const linkId = a.id.split('_')[1];

                        if (info.instancename === linkId) {
                            a.setAttribute('img-data-key', key); // a 태그에 data-key로 img 키값 넘겨주기

                            // 이벤트 등록
                            a.addEventListener("click", (event) => {

                                const divs = document.querySelectorAll('.ipb-image-div');
                                divs.forEach(div => {
                                    div.classList.add('d-none');
                                });

                                const clickedDiv = document.getElementById(key);
                                if (clickedDiv) {
                                    clickedDiv.classList.remove('d-none');
                                    ipbCurrentPage = parseInt(key.split('-').pop(), 10) || 1;

                                    updateActiveImagePage();

                                } else {
                                    console.log("클릭된 img의 div 영역을 찾을 수 없음 : ", key);
                                }

                                drawSquare(
                                    parseInt(info.x1),
                                    parseInt(info.y1),
                                    parseInt(info.x2),
                                    parseInt(info.y2),
                                    parseInt(info.instancename)
                                );
                            });
                        }
                    });
                });
            } else {
             console.warn(`infos가 배열이 아님 for key: ${key}`);
            }

        } else {
            console.warn(`parsedData에 ${key}가 없음`);
        }
    });
}

/* 사각형 그리기 */
function drawSquare(x1, y1, x2, y2, number) {

    const pngDivArea = document.querySelectorAll(".ipb-image-div:not(.d-none)");
    pngDivArea.forEach(div => {
        canvas = div.querySelector("#ipb-hotspot-canvas");
        img = div.querySelector("#ipb-image");
    });
    // canvas 크기 이미지 크기와 동일하게 설정
    canvas.width = img.clientWidth;
    canvas.height = img.clientHeight;
    ctx = canvas.getContext("2d");

    if (!canvas || !ctx) {
        console.log("Error : Canvas or Context is null!");
        return;
    }

    const scaleX = canvas.width / systemWidth;
    const scaleY = canvas.height / systemHeight;

    x1 *= scaleX;
    y1 *= scaleY;
    x2 *= scaleX;
    y2 *= scaleY;

    // 깜빡임 추가
    clearInterval(blinkInterval);
    let visible = true;
    blinkInterval = setInterval(() => {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        if (visible) {
            ctx.beginPath();
            ctx.rect(x1, y1, x2 - x1, y2 - y1);
            ctx.strokeStyle = "red";
            ctx.lineWidth = 2;
            ctx.stroke();
        }
        visible = !visible;
    }, 500); // 0.5초 마다 깜빡임

    // 20초 후에 깜빡임 중지
    setTimeout(() => {
        clearInterval(blinkInterval);
        ctx.clearRect(0, 0, canvas.width, canvas.height); // 마지막에 사각형 지우기
    }, 20000);

    // 이미지로 스크롤 이동
    canvas.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

/* 이미지 페이징 */
// 페이지네이션 생성
function createPagination(totalPages) {
    const container = document.getElementById('ipb-image-pagination');
    if (!container || totalPages < 1) return;

    const createItem = (id, content, label = '') => `
        <li class="page-item" id="${id}">
            <a class="page-link" href="#" ${label ? `aria-label="${label}"` : ''}>${content}</a>
        </li>`;

    let html = '';
    html += createItem('previousPage', '&laquo;', 'Previous');

    for (let i = 1; i <= totalPages; i++) {
        html += createItem(`page-${i}`, i);
    }

    html += createItem('nextPage', '&raquo;', 'Next');
    container.innerHTML = html;
}

function setupImagePagination(imageDataJson) {
    const parsedData = JSON.parse(imageDataJson);
    const divs = document.querySelectorAll('.ipb-image-div');

    const totalPages = divs.length;

    divs.forEach(div => {
        div.classList.add('d-none');
    });

    
    /**  2025.06.12. KTA가 아니면,  
    *    데이터가 <partinfo indexnum="0">일 경우,
    *    <retrofit>태그, <usablon>태그 시현안함. - minhee.yun 
    *    (KT1이라던가 다른 비즈코드도 적용해야는지 모르겠음. ) */
    if ('KTA' != bizCode )  {
         $("#idxnumchk").hide();
    }
    
    if (totalPages > 0) {
        // 첫 페이지 표시
        divs[ipbCurrentPage - 1].classList.remove('d-none');
        updateActiveImagePage();

        // 원 그리기 (현재 페이지의 이미지 위에 원 그리기)
        drawCircles(parsedData);

        // 페이지 번호 클릭 시 동작
        if (totalPages > 1) {
            // 페이지네이션 생성 호출
            createPagination(totalPages);

            for (let i = 1; i <= totalPages; i++) {
                document.getElementById(`page-${i}`).addEventListener('click', function(e) {
                    e.preventDefault();
                    divs[ipbCurrentPage - 1].classList.add('d-none');
                    ipbCurrentPage = i;
                    divs[ipbCurrentPage - 1].classList.remove('d-none');

                    // 페이지 변경 시 원 그리기
                    drawCircles(parsedData);

                    updateActiveImagePage();
                });
            }

            // previous 버튼 클릭 시
            document.getElementById('previousPage').addEventListener('click', function(e) {
                e.preventDefault();
                if (ipbCurrentPage > 1) {
                    divs[ipbCurrentPage - 1].classList.add('d-none');
                    ipbCurrentPage--;
                    divs[ipbCurrentPage - 1].classList.remove('d-none');

                    // 페이지 변경 시 원 그리기
                    drawCircles(parsedData);

                    updateActiveImagePage();
                }
            });

            // next 버튼 클릭 시
            document.getElementById('nextPage').addEventListener('click', function(e) {
                e.preventDefault();
                if (ipbCurrentPage < totalPages) {
                    divs[ipbCurrentPage - 1].classList.add('d-none');
                    ipbCurrentPage++;
                    divs[ipbCurrentPage - 1].classList.remove('d-none');

                    // 페이지 변경 시 원 그리기
                    drawCircles(parsedData);

                    updateActiveImagePage();
                }
            });
        }
    }
}

function updateActiveImagePage() {
    document.querySelectorAll('.page-item').forEach(item => item.classList.remove('active'));
    const activePage = document.getElementById(`page-${ipbCurrentPage}`);
    activePage?.classList.add('active');
    activePage?.querySelector('a')?.click(); // 핫스팟 누르고 페이지 넘어갈 때 원 안 그려져서 클릭되도록 추가
}


/* IPB 교범 하단 버튼 */
/* 상위 */
function goToTop() {
    const container = document.getElementById('toco_list');
    if (!container) return;

    const a = container.querySelector('a.active');
    const li = a?.closest('li');
    if (!li || !container.contains(li)) return;

    const link = li?.previousElementSibling?.querySelector('a')
        || li?.closest('ul')?.previousElementSibling;

    link?.tagName === 'A' ? link.click() : alert(ietmGoToTop);
}

/* 도해도 */
function grphViewer() {
    document.querySelector('.grphprim')?.classList.remove('d-none');
    document.querySelector('.partinfo')?.classList.add('d-none');
}

/* 테이블 */
function tableViewer() {
    document.querySelector('.partinfo')?.classList.remove('d-none');
    document.querySelector('.grphprim')?.classList.add('d-none');
}

/* 전체보기 */
function totalViewer() {
    document.querySelector('.grphprim')?.classList.remove('d-none');
    document.querySelector('.partinfo')?.classList.remove('d-none');
}

/* 항목선택 */
function ipbColsModal() {
    var modalElement = document.getElementById('ipbColsModal');
    var modal = new bootstrap.Modal(modalElement);
    modal.show();
}

/* 대체부품 팝업 */
function setReplacePartPopup(replacePartJson) {     // partnum td 에 a 링크가 포함되어 있으면 click 함수 추가
    const parsedData = JSON.parse(replacePartJson);

    const partnumTds = document.querySelectorAll('td[data-role="partnum"]');
    partnumTds.forEach(td => {
        const link = td.querySelector('a');
        if (!link) return;

        link.removeAttribute('onclick');
        // 이미 바인딩된 경우 중복 방지
        if (link.dataset.bound === "true") return;
        link.dataset.bound = "true";

        const tr = td.closest('tr'); // 현재 td가 속한 tr 요소 찾기
        const kaiStdTd = tr.querySelector('td[data-role="kai_std"]'); // kai_std td 찾기

        if (!kaiStdTd) return console.log("kai_std td not found.");

        const kaiStd = kaiStdTd.textContent.trim();
        // 해당 kaiStd 값이 repacePartXml에 있는지 확인
        const hasMatchingPart = parsedData.partbase.some(item => item.kai_std === kaiStd);

        // 매칭되는 게 없으면 <a> 태그 제거
        if (!hasMatchingPart) {
            const text = link.textContent; // 텍스트는 남기고
            td.textContent = text; // td 내용을 텍스트로 대체
            return;
        }

        link.addEventListener('click', (e) => {
            e.preventDefault();

            let html = `
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <title>IPB 교범 대체부품</title>
                <link href="/component/bootstrap/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    html, body {
                        margin: 0;
                        padding: 0;
                        background-color: #f5faff;
                    }
                    .heading-line {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        width: 100%;
                        height: 2.5rem;
                    }
                    .heading-line h3 {
                        margin: 0;
                        padding-left: 20px;
                        color: white;
                        font-size: 1.2rem;
                        font-weight: bold;
                    }
                    table {
                        border: 2px solid black;

                    }
                    th, td {
                        vertical-align: middle !important;
                    }
                </style>
            </head>
            <body>
                <div class="heading-line mb-2"
                     style="background-image: url('/img/ietm/popup/bg_pup_header.gif');
                            background-size: cover;
                            background-position: center;"
                >
                    <h3>대체부품</h3>
                </div>
                <div class="table-responsive mt-3 px-5">
                    <table class="table table-light table-bordered table-hover table-striped align-middle">
                        <thead>
                            <tr class="text-center">
                                <th scope="col">부품번호</th>
                                <th scope="col">NSN</th>
                                <th scope="col">생산자부호</th>
                            </tr>
                        </thead>
                        <tbody>
            `;

            parsedData.partbase.forEach(item => {
                if (item.kai_std === kaiStd) {
                    const replacePartnum = item.partnum;
                    const replaceNsn = item.nsn;
                    const replaceCage = item.cage;

                    html += `
                            <tr class="text-center">
                                <td>${replacePartnum}</td>
                                <td>${replaceNsn}</td>
                                <td>${replaceCage}</td>
                            </tr>
                    `;
                }
            });

            html += `
                            </tbody>
                        </table>
                    </div>
                </body>
                </html>
            `;

            openReplacePopup(html, 600, 300);
        });
    });
}
// 팝업창 오픈
function openReplacePopup(content, width, height) {
    const popup = window.open('', '', `width=${width},height=${height}`);
    if (!popup) return alert(ietmOpenPopupFail);
    popup.document.write(content);
    popup.document.close();
}