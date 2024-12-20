import { Answer } from "../answers/answer.entity";
import { Quiz } from "../quizzes/quiz.entity";
export declare enum QuestionType {
    SINGLE_ANSWER = "SINGLE_ANSWER",
    MULTIPLE_ANSWER = "MULTIPLE_ANSWER",
    ORDERED_ANSWER = "ORDERED_ANSWER",
    OPEN_ANSWER = "OPEN_ANSWER"
}
export declare class Question {
    id: number;
    questionString: string;
    type: QuestionType;
    quizId: number;
    quiz: Quiz;
    answers: Answer[];
}
