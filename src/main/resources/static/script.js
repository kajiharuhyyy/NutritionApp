function calculate(){
    const foodGroups = document.querySelectorAll(".food-group");
    const requestList = [];

    foodGroups.forEach(group =>{
      const name = group.querySelector('input[name="foodName"]').value;
      const amount = parseFloat(group.querySelector('input[name="amount"]').value);
      if (name && !isNaN(amount)) {
        requestList.push({name, amount });
      }
    });

  fetch("http://localhost:8080/api/foods/calculate-multi", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Accept": "application/json"
    },
    body: JSON.stringify(requestList)
  })
  .then(response => response.json())
  .then(data => {
    const resultElement = document.getElementById("result");
    resultElement.innerHTML = "";

    let total = {
      amount: 0,
      energy: 0,
      protein: 0,
      fat: 0,
      carbohydrates: 0,
      salt: 0
    };

    data.forEach(item => {
      const div = document.createElement("div")
      div.innerHTML=`
  食品名: ${item.name}<br>
  量: ${item.amount} g<br>
  エネルギー: ${item.energy} kcal<br>
  たんぱく質: ${item.protein} g<br>
  脂質: ${item.fat} g<br>
  炭水化物: ${item.carbohydrates} g<br>
  食塩相当量: ${item.salt} g<br>
  <hr>
      `;
      resultElement.appendChild(div);

      total.amount += item.amount;
      total.energy += item.energy;
      total.protein += item.protein;
      total.fat += item.fat;
      total.carbohydrates += item.carbohydrates;
      total.salt += item.salt;
    });

    const totalDiv = document.createElement("div");
    totalDiv.innerHTML =`
    <strong> 【合計】</strong><br>
    総量: ${total.amount} g<br>
    総エネルギー: ${total.energy.toFixed(2)} kcal<br>
    総たんぱく質: ${total.protein.toFixed(2)} g<br>
    総脂質: ${total.fat.toFixed(2)} g<br>
    総炭水化物: ${total.carbohydrates.toFixed(2)} g<br>
    総食塩相当量: ${total.salt.toFixed(2)} g<br>
    `;
    resultElement.appendChild(totalDiv);
  })
  .catch(error => {
    document.getElementById("result").innerText = error;
  });
  }

window.onload = function () {
  fetch("http://localhost:8080/api/foods")
  .then(response => response.json())
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

function addFoodInput() {
  const div = document.createElement("div");
  div.classList.add("food-group");
  div.innerHTML = `
    <input list="foodList" name="foodName" placeholder="食品名" />
    <input type="number" name="amount" placeholder="g" />
  `;
  document.getElementById("foodInputs").appendChild(div);
}
