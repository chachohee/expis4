/**
 * 호출 함수
 * 2025.04.15 - osm
 * - 이미지 존재 시 하단 제어 버튼 (저장, 출력, 새창) 생성
 * - 이미지 미존재 시 대체 이미지(noimg)를 출력하고 하단 제어 버튼 (저장, 출력, 새창) 숨김
 * - 동영상에 대한 버튼 아직 미구현
 */
function imageEventBind() {
    document.querySelectorAll('.img-control').forEach(function (img) {
        // 경로에 이미지 없을 시 noimg.png 출력 및 하단 제어 버튼 미생성
        img.onerror = function () {
            this.src = '/img/common/noimg.png'; // 임시 하드코딩
            this.dataset.error = 'noimg';
        };

        // 이미지 정상출력 시 하단 제어 버튼 생성
        img.onload = function () {
            if (!this.dataset.error) {
                const controlBox = this.closest('.gs-grphprim')?.querySelector('.img_controll');
                if (controlBox) controlBox.style.display = 'block';
            }
        };
    });
}

/**
 * 2025.04.16 - osm
 * 이미지 다운로드
 * xslt onclick 이벤트 함수
 */
function imageDownload(src) {
    console.log("imageDownload CALL : ", src);
    location.href="imageDownload.do?filePath=" + encodeURIComponent(src);
}

/**
 * 2025.04.16 - osm
 * - 이미지 출력 (이미지, 이미지 명)
 * - 테이블(표) 출력 (테이블 htmlContent)
 * - xslt onclick 이벤트 함수
 * - 출력에 쓰이는 템플릿을 함수로 정의 -> generatePrintTemplate
 */
function imagePrint(src, text) {
    openPrintWindow('image', src, text);
}

function tablePrint(content) {
    openPrintWindow('table', content);
}

function openPrintWindow(type, content, text) {
    const features = "width=1500px, height=1200px, left=500, top=100, toolbar=no, menubar=no, resizable=yes";
    const printWindow = window.open('', '_blank', features);
    printWindow.document.write(generatePrintTemplate(type, content, text));
    printWindow.document.close();
}

/* 출력에 사용되는 html 템플릿 함수 */
function generatePrintTemplate(type, content, text) {
    const bodyContent = {
        'image': `<img src="${content}" alt="image"/><div>${text}</div>`,
        'table': `<div class="table-wrap">${content}</div>`,
    }[type]|| '';

    return `
        <html lang="ko">
        <head>
            <title>eXPIS-III</title>
            <style>
                body { margin: 0; padding: 5px; text-align: center; }
                img { max-width: 100%; height: auto; }
                table { width: 100%; border-collapse: collapse; text-align: center;}
                th, td { border: 1px solid #ccc; padding: 8px; }
            </style>
        </head>
        <body onload="window.print(); window.close();">
            ${bodyContent}
        </body>
        </html>
    `;
}
/* --- */
