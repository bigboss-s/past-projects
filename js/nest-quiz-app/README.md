# ONLINE QUIZ NEST.JS LEARNING ASSIGNMENT
## Quiz API instructions
* database environment variables are located in the `.env` file
* to run the API:
 ```   
 npm start run
 ```
* to access the GraphQL Playground:
```
http://localhost:3000/graphql
```
* to run the sample test suite
```
npm test
```

## Example queries and mutations
### Sample quiz creation mutation
```
mutation{
  createQuiz(CreateQuizInput: {
    name: "test quiz",
    description: "this is a test quiz",
    questions: [
      {
        questionString: "single answer question",
        type: SINGLE_ANSWER,
        answers: [
          {answerString: "answer is false", isCorrect: false},
          {answerString: "answer is true", isCorrect: true}
        ]
      },
      {
        questionString: "multiple answer question",
        type: MULTIPLE_ANSWER,
        answers: [
          {answerString: "answer is true", isCorrect: true},
          {answerString: "answer is true", isCorrect: true}
        ]
      },
      {
        questionString: "open answer question, answer is 'some string'",
        type: OPEN_ANSWER,
        answers: [
          {answerString: "some string"}
        ]
      },
      {
        questionString: "sorting answer question",
        type: ORDERED_ANSWER,
        answers: [
          {answerString: "order is 1", order: 1},
          {answerString: "order is 2", order: 2},
          {answerString: "order is 4", order: 4},
          {answerString: "order is 3", order: 3}
        ]
      }
    ]
  })
  {
    id,
    name,
    description,
    questions{
      id,
      questionString,
      type,
      answers{
        id,
        answerString,
        isCorrect,
        order
      }
    }
  }
}
```
### Get all quizzes query
```
{
  getQuizzes{
    id,
    name,
    description,
    questions{
      id,
      questionString,
      type,
      answers{
        id,
        answerString,
        isCorrect
      }
    }
  }
}
```
### Get full quiz by id query
```
{
  getFullQuiz(id: 2){
    id,
    name,
    description,
    questions{
      id,
      questionString,
      type,
      answers{
        id,
        answerString,
        isCorrect,
        order
      }
    }
  }
}
```
### Sample show quiz without correct answers query
```
{
  showQuiz(id: 4){
    id,
    name,
    description,
    questions{
      id,
      questionString,
      type,
      answers{
        id,
        answerString
      }
    }
  }
}
```
### Sample check quiz query
```
{
  checkQuiz(ReturnQuizDTO: {
    id: 4,
    questions: [
      {
        id: 10,
        type: SINGLE_ANSWER,
        answers: [
          {
            id: 20,
            isCheckedTrue: false
          },
          {
            id: 21,
            isCheckedTrue: true
          }
        ]
      },
      {
        id: 11,
        type: MULTIPLE_ANSWER,
        answers: [
          {
            id: 22,
            isCheckedTrue: true
          },
          {
            id: 23,
            isCheckedTrue: true
          }
        ]
      },
      {
        id: 12,
        type: OPEN_ANSWER,
        answers: [
          {
            id: 24,
            answerString: "some string"
          }
        ]
      },
      {
        id: 13,
        type: ORDERED_ANSWER,
        answers: [
          {
            id: 25,
            checkedOrder: 1
          },
          {
            id: 26,
            checkedOrder: 2
          },
          {
            id: 27,
            checkedOrder: 4
          },
          {
            id: 28,
            checkedOrder: 3
          }
        ]
      }
    ]
  }){
    scoredPoints,
    maxPoints
  }
}
```