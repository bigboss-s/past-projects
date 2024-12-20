import { AnswersService } from './answers.service';
import { Answer } from './answer.entity';
export declare class AnswersResolver {
    private answersService;
    constructor(answersService: AnswersService);
    answers(): Promise<Answer[]>;
}
