document.addEventListener("DOMContentLoaded", function() {
    const orderBtn = document.getElementById('orderBtn');
    const orderPopup = document.getElementById('orderPopup');
    const cancelBtn = document.getElementById('cancelBtn');
    const orderForm = document.getElementById('orderForm');
    const orderTable = document.querySelector('.board-table tbody');
    const deleteBtn = document.getElementById('deleteBtn');
    const graphBtn = document.getElementById('graphBtn');
    const graphPopup = document.getElementById('graphPopup');
    let selectedRows = [];
    const btnForm = document.querySelector('#btnForm');

    // 주문 접수 버튼 클릭 시 팝업 열기
    orderBtn.addEventListener('click', function(event) {
        event.preventDefault();
        orderPopup.style.display = 'block';  // 팝업 표시
    });

    // 그래프 버튼 클릭 시 팝업 열기
    graphBtn.addEventListener('click', function(event) {
        graphPopup.style.display = 'block';
        fetch('/orders/api/productiongraph')
            .then(response => response.json())
            .then(data => {
                orderData = data;
                createGraph();
            })
            .catch(error => console.error('Error loading graph data:', error));
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
        if (event.target === graphPopup) {
            graphPopup.style.display = 'none';
        }
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