function calculate(){
    const name = document.getElementById("foodName").value;
    const amount = parseFloat(document.getElementById("amount").value);

    fetch("http://localhost:8080/api/foods/calculate",{
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
      body: JSON.stringify({name, amount })
    })
    .then(response => {
      if (!response.ok) {
        throw new Error("計算に失敗しました");
      }
      return response.json();
    })
    .then(data => {
      document.getElementById("result").innerText = `
        食品名:　${data.name}
        量: ${data.amount} g
        エネルギー: ${data.energy} kcal
        たんぱく質: ${data.protein} g
        脂質: ${data.fat} g
        炭水化物:　${data.carbohydrates} g
        食塩相当量:　${data.salt} g
    `;
    })
    .catch(error => {
      document.getElementById("result").innerText = error;
    });
  }
  window.onload = function (){
  fetch("http://localhost:8080/api/foods")
    .then(response =>response.json())
    .then(data => {
      const dataList = document.getElementById("foodList");
      dataList.innerHTML = "";
      data.forEach(item => {
        const option = document.createElement("option");
        option.value = item.name;
        dataList.appendChild(option);
      });
    })
    .catch(error => {
      console.error("食品一覧取得失敗:", error);
    });
};