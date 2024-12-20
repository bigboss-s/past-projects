import { Question } from './questions.entity';
import { QuestionsService } from './questions.service';
export declare class QuestionsResolver {
    private questionsService;
    constructor(questionsService: QuestionsService);
    questions(): Promise<Question[]>;
}
