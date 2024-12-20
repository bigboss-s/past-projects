import { ShowQuestionDTO } from "../../questions/dto/show-question.type";
export declare class ShowQuizDTO {
    id: number;
    name: string;
    description: string;
    questions: ShowQuestionDTO[];
}
