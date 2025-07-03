let globalPfcTargets = null;

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
  const saltTarget = getSaltTarget(gender);

  const targets = {
    protein: {
      min: (energyNeed * 0.13 / 4).toFixed(1),
      max: (energyNeed * 0.20 / 4).toFixed(1)
    },
    fat: {
      min: (energyNeed * 0.20 / 9).toFixed(1),
      max: (energyNeed * 0.30 / 9).toFixed(1)
    },
    carbohydrates: {
      min: (energyNeed * 0.50 / 4).toFixed(1),
      max: (energyNeed * 0.65 / 4).toFixed(1)
    }
  };


  document.getElementById("energyNeedResult").innerText =
      `基礎代謝量: ${bmr.toFixed(0)} kcal/日\n` +
      `推定必要エネルギー量: ${energyNeed.toFixed(0)} kcal/日\n` +
      `たんぱく質: ${targets.protein.min}g ~ ${targets.protein.max}g\n`+
      `脂質: ${targets.fat.min}g ~ ${targets.fat.max}g\n` +
      `炭水化物: ${targets.carbohydrates.min}g ~ ${targets.carbohydrates.max}g\n` +
      `塩分目標: ${saltTarget}g未満`;

  globalPfcTargets = targets;
}

function getSaltTarget(gender) {
  if (gender === "male") return 7.5;
  if (gender === "female") return 6.5;
  return 7.0;
}

function calculate(){
  if(!globalPfcTargets) {
    alert("先に必要エネルギー量を計算してください。");
    return;
  }

    const foodGroups = document.querySelectorAll(".food-group");
    const requestList = [];
    const gender = document.getElementById("gender").value;
    const saltTarget = getSaltTarget(gender);
    let saltFeedback = "";

    foodGroups.forEach(group =>{
      const name = group.querySelector('input[name="foodName"]').value;
      const amount = parseFloat(group.querySelector('input[name="amount"]').value);
      if (name && !isNaN(amount)) {
        requestList.push({name, amount });
      }
    });

  fetch("/api/foods/calculate-multi", {
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

    if (total.salt <= saltTarget) {
      saltFeedback = `<span style="color:green;"> 塩分摂取量は目標以内です。</span>`;
    } else {
      saltFeedback = `<span style="color:red;"> 塩分摂取量は目標を超えています。</span>`;
    }

  if(globalPfcTargets) {
    const comparisonDiv =document.createElement("div");
    comparisonDiv.innerHTML = `
      <strong>【目標との比較】</strong><br>
      たんぱく質:${total.protein.toFixed(1)} g (目標:${globalPfcTargets.protein.min}g ~ ${globalPfcTargets.protein.max}g) <br>
      脂質:${total.fat.toFixed(1)} g (目標:${globalPfcTargets.fat.min}g ~ ${globalPfcTargets.fat.max}g) <br>
      炭水化物:${total.carbohydrates.toFixed(1)} g (目標:${globalPfcTargets.carbohydrates.min}g ~ ${globalPfcTargets.carbohydrates.max}g) <br>
    `;
    resultElement.appendChild(comparisonDiv);
  }

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
    totalDiv.innerHTML += `<br>${saltFeedback}`

    fetch("/api/foods/pfc-ratio", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    })
    .then(res => res.json())
    .then(pfc => {
      updatePfcChart(pfc);
    })
    .catch(error => console.error("pfc計算エラー:", error));

    fetch("/api/foods/save-result", {
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
  fetch("/api/foods")
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

  fetch("/api/foods/history")
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

  fetchDailyPfcSummary();
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

function updatePfcChart(pfc) {
  const chart = document.getElementById('pfcChart');
  const label = document.getElementById('pfcLabel');

  chart.style.background = `conic-gradient(
    #f66 0% ${pfc.proteinRatio}%,
    #6f6 ${pfc.carbohydrateRatio}% ${pfc.proteinRatio + pfc.fatRatio}%,
    #66f ${pfc.proteinRatio + pfc.fatRatio}% 100%
  )`;

  label.innerHTML = `
    <strong>PFCバランス</strong><br>
    タンパク質: ${pfc.proteinRatio.toFixed(1)}%<br>
    脂質: ${pfc.fatRatio.toFixed(1)}%<br>
    炭水化物: ${pfc.carbohydrateRatio.toFixed(1)}%
  `;
}

function fetchDailyPfcSummary() {
  fetch("/api/foods/history/daily-summary")
  .then(response => response.json())
  .then(data => {
    const labels = data.map(item => item.date);
    const proteinData = data.map(item => item.protein * 4);
    const fatData = data.map(item => item.fat * 9);
    const carbData = data.map(item => item.carbohydrates * 4);

    const ctx = document.getElementById('dailyPfcChart').getContext('2d');
    new Chart(ctx, {
      type: 'bar',
      data: {
        labels : labels,
        datasets : [
          {
            label : 'たんぱく質(kcal)',
            data : proteinData,
            stack : 'stack1'
          },
          {
            label : '脂質(kcal)',
            data : fatData,
            stack : 'stack1'
          },
          {
            label : '炭水化物(kcal)',
            data : carbData,
            stack : 'stack1'
          }
        ]
      },
      options : {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: '日別PFCバランス(kcal)'
          }
        },
        scales: {
          x: { stacked: true},
          y: { stacked: true, title: { display: true, text: 'kcal' } }
        }
      }
    });
  })
  .catch(err => console.error("日別PFCエラー:", err));
}