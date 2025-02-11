document.addEventListener("DOMContentLoaded", function() {
    const orderBtn = document.getElementById('orderBtn');
    const orderPopup = document.getElementById('orderPopup');
    const cancelBtn = document.getElementById('cancelBtn');
    const orderForm = document.getElementById('orderForm');
    const orderTable = document.querySelector('.board-table tbody');

    // 주문 접수 버튼 클릭 시 팝업 열기
    orderBtn.addEventListener('click', function() {
        orderPopup.style.display = 'block';  // 팝업 표시
    });

    // 취소 버튼 클릭 시 팝업 닫기
    cancelBtn.addEventListener('click', function() {
        orderPopup.style.display = 'none';  // 팝업 숨기기
    });

    // 팝업 외부 클릭 시 팝업 닫기
    window.addEventListener('click', function(event) {
        if (event.target === orderPopup) {
            orderPopup.style.display = 'none';  // 팝업 외부 클릭 시 팝업을 닫기
        }
    });

    // 폼 제출 처리
    orderForm.addEventListener('submit', function(event) {
        event.preventDefault();  // 기본 폼 제출 방지

        const mname = document.getElementById('mname').value;
        const mcategory = document.getElementById('mcategory').value;
        const mquantity = document.getElementById('mquantity').value;
        const mprice = document.getElementById('mprice').value;
        const mwarehouse = document.getElementById('mwarehouse').value;
        const mstockstatus = document.getElementById('mstockstatus').value;

        const formData = new FormData();
        formData.append("mname", mname);
        formData.append("mcategory", mcategory);
        formData.append("mquantity", mquantity);
        formData.append("mprice", mprice);
        formData.append("mwarehouse", mwarehouse);
        formData.append("mstockstatus", mstockstatus);

        // 데이터를 서버로 보내서 저장
        fetch('/orders/create', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())  // 서버에서 받은 JSON 데이터 처리
        .then(data => {
            if (data.success) {
                alert('주문이 추가되었습니다.');

                // 새로운 주문을 테이블에 추가
                const newRow = document.createElement('tr');
                newRow.innerHTML = `
                    <td>${data.material.mname}</td>
                    <td>${data.material.mcategory}</td>
                    <td>${data.material.mquantity}</td>
                    <td>${data.material.mprice}</td>
                    <td>${data.material.mwarehouse}</td>
                    <td>${data.material.mstockstatus}</td>


                `;

                // 테이블에 새로운 행 추가
                orderTable.appendChild(newRow);

                // 팝업 닫고 폼 초기화
                orderPopup.style.display = 'none';
                orderForm.reset();
            } else {
                alert('자재 추가에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('서버와의 연결에 문제가 발생했습니다.');
        });
    });
});