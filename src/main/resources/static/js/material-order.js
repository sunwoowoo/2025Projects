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

    // 자재 추가 버튼 클릭 시 팝업 열기
    orderBtn.addEventListener('click', function(event) {
        event.preventDefault();
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
    // 그래프 팝업 외부 클릭 시 팝업 닫기
    window.addEventListener('click', function(event) {
            if (event.target === graphPopup) {
                graphPopup.style.display = 'none';  // 팝업 외부 클릭 시 팝업을 닫기
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
                     document.querySelector("#mid").value = JSON.stringify(orderIds);

                     btnForm.action = `/materials/remove`;
                     btnForm.method = 'post';
                     btnForm.submit();

                     selectedRows = [];	// 선택된 행들 집합 리셋
                 } else {
                    selectedRows = [];	// 선택된 행들 집합 리셋
                 }
             }
          }, false);

    // 그래프 생성
    graphBtn.addEventListener('click', function(event) {
        graphPopup.style.display = 'block'; //팝업 표시
        fetch('materials/api/materialgraph')
            .then(response => response.json())
            .then(data => {
                const processTypes = ['PRESSING', 'WELDING', 'PAINTING', 'ASSEMBLING'];
                const processColors = [
                    'rgb(255, 99, 132)',  // Red for PRESSING
                    'rgb(54, 162, 235)',  // Blue for WELDING
                    'rgb(255, 206, 86)',  // Yellow for PAINTING
                    'rgb(75, 192, 192)'   // Green for ASSEMBLING
                ];

                // Aggregate materials with the same name and process
                const aggregatedData = data.reduce((acc, item) => {
                    const key = `${item.mname}-${item.mprocess}`;
                    if (!acc[key]) {
                        acc[key] = {...item};
                    } else {
                        acc[key].mquantity += item.mquantity;
                    }
                    return acc;
                }, {});

                // Prepare data for outer ring (ProcessTypes)
                const outerData = processTypes.map(process =>
                    Object.values(aggregatedData)
                        .filter(item => item.mprocess === process)
                        .reduce((sum, item) => sum + item.mquantity, 0)
                );

                // Prepare data for inner ring (Materials)
                const sortedData = Object.values(aggregatedData).sort((a, b) =>
                    processTypes.indexOf(a.mprocess) - processTypes.indexOf(b.mprocess)
                );
                const innerData = sortedData.map(item => item.mquantity);
                const innerLabels = sortedData.map(item => item.mname);
                const innerColors = sortedData.map(item => {
                    const processIndex = processTypes.indexOf(item.mprocess);
                    return lightenColor(processColors[processIndex], 0.7);
                });

                const ctx = document.getElementById('materialChart').getContext('2d');
                new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        datasets: [{
                            data: outerData,
                            backgroundColor: processColors,
                            label: 'Process Types'
                        }, {
                            data: innerData,
                            backgroundColor: innerColors,
                            label: 'Materials'
                        }],
                        labels: innerLabels
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                position: 'right',
                                labels: {
                                    generateLabels: function(chart) {
                                        return processTypes.map((type, i) => ({
                                            text: `${type}: ${outerData[i].toLocaleString()}`,
                                            fillStyle: processColors[i],
                                            hidden: false,
                                            index: i
                                        }));
                                    }
                                }
                            },
                            title: {
                                display: true,
                                text: '공정별 자재소요표'
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const datasetIndex = context.datasetIndex;
                                        const index = context.dataIndex;
                                        if (datasetIndex === 0) {
                                            return `${processTypes[index]}: ${context.formattedValue}`;
                                        } else {
                                            const material = sortedData[index];
                                            return `${material.mname}: ${context.formattedValue}`;
                                        }
                                    },
                                    title: function(context) {
                                        const datasetIndex = context[0].datasetIndex;
                                        const index = context[0].dataIndex;
                                        if (datasetIndex === 0) {
                                            return processTypes[index];
                                        } else {
                                            return sortedData[index].mprocess;
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => console.error('Error:', error));
    });

    function lightenColor(color, factor) {
        const rgb = color.match(/\d+/g).map(Number);
        const lightenedRgb = rgb.map(value => Math.round(value + (255 - value) * factor));
        return `rgb(${lightenedRgb.join(', ')})`;
    }
});

