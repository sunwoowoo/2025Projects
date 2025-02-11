document.addEventListener("DOMContentLoaded", function() {
    const orderBtn = document.getElementById('orderBtn');
    const orderPopup = document.getElementById('orderPopup');
    const cancelBtn = document.getElementById('cancelBtn');
    const orderForm = document.getElementById('orderForm');
    const orderTable = document.querySelector('.board-table tbody');
    const deleteBtn = document.getElementById('deleteBtn');
    let selectedRows = [];
    const btnForm = document.querySelector('#btnForm');

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

        const model = document.getElementById('carModel').value;
        const quantity = document.getElementById('quantity').value;
        const orderStatus = document.getElementById('orderStatus').value;

        const formData = new FormData();
        formData.append("carModel", model);
        formData.append("quantity", quantity);
        formData.append("orderStatus", orderStatus);

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
                    <td>${data.order.id}</td>
                    <td>${data.order.model}</td>
                    <td>${data.order.orderStatus}</td>
                    <td>${data.order.processType}</td>
                    <td><div class="progress-bar" style="width:${data.order.progress}%;">${data.order.progress}%</div></td>
                    <td>${data.order.quantity}</td>
                    <td>${data.order.startDate}</td>
                    <td>${data.order.endDate}</td>
                    <td><a href="#">Simulate</a></td>
                `;

                // 테이블에 새로운 행 추가
                orderTable.appendChild(newRow);

                // 팝업 닫고 폼 초기화
                orderPopup.style.display = 'none';
                orderForm.reset();
            } else {
                alert('주문 추가에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('서버와의 연결에 문제가 발생했습니다.');
        });
    });

    //행 선택 동작
    document.querySelectorAll('.board-table > tbody > tr').forEach(row => {
        row.addEventListener("click", function(event) {
            event.stopPropagation();
            //단일행 선택 동작
            if (selectedRows.includes(this)) {
                //선택 해제
                selectedRows = selectedRows.filter(r => r != this);
                this.classList.remove('selected');
                console.log("selected Rows", selectedRows);

            } else {
                //행 선택
                selectedRows.forEach(r => r.classList.remove('selected'));
                selectedRows = [this];
                this.classList.add('selected');
                console.log("selected Rows", this);
            }
        });
    });

    // 아무 화면 누를 시 선택 해제
    window.addEventListener('click', function(event) {
        // 모든 행 비선택
        selectedRows.forEach(row => row.classList.remove('selected'));
        selectedRows = [];
        console.log("Selected Rows Cleared:", selectedRows);
    });

    // 행 삭제 버튼
    deleteBtn.addEventListener("click", function(e){
            e.preventDefault();
            e.stopPropagation();
            console.log("Delete Button clicked:")

    		 if (selectedRows.length == 0) {	// 선택한 행이 없을 시
    			 alert("항목을 선택해주세요.");
    		 } else {
    			 if (confirm("정말 삭제하시겠습니까?")) {
    				 for (i = 0; i < selectedRows.length; i++) {	// 모든 선택된 행
    					 const orderId = selectedRows[i].children[0].textContent;
    					 selectedRows[i].remove();
    					 console.log("Deleting an order with ID:", orderId);

    					 // Set hidden input value
                         document.querySelector("#orderId").value = orderId;

    				 	 btnForm.action = `/orders/remove`;
    				 	 btnForm.method = 'post';
    				 	 btnForm.submit();
    				 }
    				 selectedRows = [];	// 선택된 행들 집합 리셋
    			 } else {
    			    selectedRows = [];	// 선택된 행들 집합 리셋
    			 }
    		 }
    	  }, false);
});