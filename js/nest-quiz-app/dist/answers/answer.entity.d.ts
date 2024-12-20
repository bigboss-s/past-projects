import { Question } from "../questions/questions.entity";
export declare class Answer {
    id: number;
    answerString: string;
    isCorrect?: boolean;
    order?: number;
    questionId: number;
    question: Question;
}
