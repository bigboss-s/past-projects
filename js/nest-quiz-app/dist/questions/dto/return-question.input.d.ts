import { ReturnAnswerDTO } from "../../answers/dto/return-answer.input";
import { QuestionType } from "../questions.entity";
export declare class ReturnQuestionDTO {
    id: number;
    type: QuestionType;
    answers: ReturnAnswerDTO[];
}
