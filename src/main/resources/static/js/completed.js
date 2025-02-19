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
    let lastSelectedRow = null;

    fetch('/completed-orders/completed')
      .then(response => response.json())
      .then(data => {
        console.log(data);
      });

    // 그래프 버튼 클릭 시 팝업 열기
    graphBtn.addEventListener('click', function(event) {
        graphPopup.style.display = 'block'; //팝업 표시
        fetch('/completed-orders/api/completedgraph')
                    .then(response => response.json())
                    .then(data => {
                        orderData = data;
                        createGraph();
                    })
                    .catch(error => console.error('Error loading graph data:', error));
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
            // 다수행 선택 동작
            if (event.shiftKey && lastSelectedRow) {
                // Shift key is pressed and we have a last selected row
                const rows = Array.from(this.parentElement.children);
                const currentIndex = rows.indexOf(this);
                const lastIndex = rows.indexOf(lastSelectedRow);

                const start = Math.min(currentIndex, lastIndex);
                const end = Math.max(currentIndex, lastIndex);

                selectedRows = rows.slice(start, end + 1);

                // Clear previous selections
                rows.forEach(r => r.classList.remove('selected'));

                // Add 'selected' class to the new selection
                selectedRows.forEach(r => r.classList.add('selected'));
            } else {
                //단일행 선택 동작
                if (selectedRows.includes(this)) {
                    //선택 해제
                    selectedRows = selectedRows.filter(r => r != this);
                    this.classList.remove('selected');
                    console.log("selected Rows", selectedRows);
                } else {
                    //행 선택
                    if (!event.ctrlKey) {
                        // If Ctrl (or Cmd on Mac) is not pressed, clear previous selections
                        selectedRows.forEach(r => r.classList.remove('selected'));
                        selectedRows = [];
                    }
                    selectedRows.push(this);
                    this.classList.add('selected');
                }

                lastSelectedRow = this;
                console.log("Selected Rows", selectedRows);
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
    			     const orderIds = selectedRows.map(row => row.children[0].textContent.trim());

    				 // Remove all selected rows from the DOM
                     selectedRows.forEach(row => row.remove());

                     // Set hidden input value with all orderIds (JSON)
                     document.querySelector("#orderId").value = JSON.stringify(orderIds);

                     btnForm.action = `/completed-orders/remove`;
                     btnForm.method = 'post';
                     btnForm.submit();

    				 selectedRows = [];	// 선택된 행들 집합 리셋
    			 } else {
    			    selectedRows = [];	// 선택된 행들 집합 리셋
    			 }
    		 }
    	  }, false);

    // 검색 조건을 날짜검색으로 바꿀 시 검색상자를 날짜 형식으로 변환
    document.getElementById('searchType').addEventListener('change', function() {
        const searchInput = document.getElementById('searchInput');
        const dateInput = document.getElementById('dateInput');
        console.log("dateInput : ", dateInput);

        console.log("Search type changed to:", this.value);
        if (this.value === 'regDate') {
            searchInput.style.display = 'none';
            dateInput.style.display = 'inline-block';
             console.log("Date input value:", dateInput.value);
        } else {
            searchInput.style.display = 'inline-block';
            dateInput.style.display = 'none';
        }
    });
});