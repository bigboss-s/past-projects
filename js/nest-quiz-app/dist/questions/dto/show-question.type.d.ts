import { QuestionType } from "../questions.entity";
import { ShowAnswerDTO } from "../../answers/dto/show-answer.type";
export declare class ShowQuestionDTO {
    id: number;
    questionString: string;
    type: QuestionType;
    answers?: ShowAnswerDTO[];
}
