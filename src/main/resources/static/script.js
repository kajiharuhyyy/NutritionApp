function calculateStandardWeight() {
  const height = parseFloat(document.getElementById("heightInput").value);
  if (isNaN(height) || height <= 0) {
    document.getElementById("weightResult").innerText = "正しい身長を入力してください";
    return;
  }

  const standardWeight = height * height * 22;
  document.getElementById("weightResult").innerText = `標準体重は${standardWeight.toFixed(2)}kgです`;
}

function calculateEnergyNeed() {
  const age = parseInt(document.getElementById("age").value);
  const gender = document.getElementById("gender").value;
  const weight = parseFloat(document.getElementById("weight").value);
  const activity = parseFloat(document.getElementById("activityLevel").value);

  if (isNaN(age) || isNaN(weight)) {
    document.getElementById("energyNeedResult").innerText = "年齢と体重を正しく入力してください";
    return;
  }

  let bmr;

  // 簡易的な日本人向け基礎代謝量（厚労省推奨値ベース）
  if (gender === "male") {
    if (age < 30) bmr = 15.3 * weight + 679;
    else if (age < 60) bmr = 11.6 * weight + 879;
    else bmr = 13.5 * weight + 487;
  } else {
    if (age < 30) bmr = 14.7 * weight + 496;
    else if (age < 60) bmr = 8.7 * weight + 829;
    else bmr = 10.5 * weight + 596;
  }

  const energyNeed = bmr * activity;

  document.getElementById("energyNeedResult").innerText =
      `基礎代謝量: ${bmr.toFixed(0)} kcal/日\n` +
      `推定必要エネルギー量: ${energyNeed.toFixed(0)} kcal/日`;
}


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
      量: ${item.amount.toFixed(2)} g<br>
      エネルギー: ${item.energy.toFixed(2)} kcal<br>
      たんぱく質: ${item.protein.toFixed(2)} g<br>
      脂質: ${item.fat.toFixed(2)} g<br>
      炭水化物: ${item.carbohydrates.toFixed(2)} g<br>
      食塩相当量: ${item.salt.toFixed(2)} g<br>
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

    fetch("http://localhost:8080/api/foods/save-result", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    })
    .then(res => res.text())
    .then(message => {
      console.log("保存メッセージ:", message);
    })
    .catch(err => console.error("保存エラー:", err));
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

  fetch("http://localhost:8080/api/foods/history")
  .then(response => response.json())
  .then(history => {
    const historyElement = document.getElementById("history");
    historyElement.innerHTML = "<h2>履歴</h2>";
    history.forEach(item => {
      const div = document.createElement("div");
      div.innerHTML = `
          食品名: ${item.name}<br>
          量: ${item.amount} g<br>
          エネルギー: ${item.energy.toFixed(2)} kcal<br>
          <hr>
        `;
      historyElement.appendChild(div);
    });
  })
  .catch(error => {
    console.error("履歴取得失敗:", error);
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
