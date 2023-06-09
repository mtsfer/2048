const boardElement = document.querySelector("[data-board]")
const alertContainerElement = document.querySelector("[data-alert-container]")
const scoreElement = document.querySelector("[data-score]")
const baseURL = "http://localhost:8080/api/game"

startInteraction()

function startInteraction() {
    document.addEventListener("keydown", handleKeyPress)
    console.log("TESTE")
    getInitialBoard()
}

function stopInteraction() {
    document.removeEventListener("keydown", handleKeyPress)
}

function handleKeyPress(event) {
    console.log("TESTE")
    if (event.key === "ArrowLeft") {
        getBoardAfterMove('LEFT')
    } else if (event.key === "ArrowUp") {
        getBoardAfterMove('UP')
    } else if (event.key === "ArrowRight") {
        getBoardAfterMove('RIGHT')
    } else if (event.key === "ArrowDown") {
        getBoardAfterMove('DOWN')
    }
}

function getInitialBoard() {
    const response = fetch(baseURL, {
        method: "GET",
        mode: "no-cors",
        credentials: "same-origin"
    })
        .then(function (response) { return response.json() })
        .then(fromJsonToBoard)
}

function getBoardAfterMove(direction) {
    const response = fetch(baseURL, {
        method: 'POST',
        body: direction,
        headers: new Headers({'content-type': 'text/plain'}),
        mode: "no-cors",
        credentials: "same-origin"
    })
        .then(function (response) { return response.json() })
        .then(fromJsonToBoard)
}

function fromJsonToBoard(json) {
    const tiles = boardElement.children
    scoreElement.textContent = "Score: " + json['score']
    const boardJson = json['board']
    const status = json['status']
    console.log(status)
    if (status === 'WIN') {
        alertContainerElement.textContent = "Congratulations!"
        stopInteraction()
    } else if (status === 'LOST') {
        alertContainerElement.textContent = "You lost!"
        stopInteraction()
    } else {
        for (const index in boardJson) {
            const rowNumber = boardJson[index]['row']
            console.log(rowNumber)
            const values = boardJson[index]['values']
            for (const value in values) {
                const tileIndex = 4 * (rowNumber - 1) + parseInt(value) - 1
                const currentNumber = values[value]
                if (currentNumber !== 0) {
                    tiles.item(tileIndex).textContent = values[value]
                } else {
                    tiles.item(tileIndex).textContent = ''
                }
            }
        }
    }

}