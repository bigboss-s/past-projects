import { Question } from "../questions/questions.entity";
export declare class Quiz {
    id: number;
    name: string;
    description: string;
    questions?: Question[];
}
