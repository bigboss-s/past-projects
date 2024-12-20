import { Question } from './questions.entity';
import { Repository } from 'typeorm';
import { Answer } from '../answers/answer.entity';
export declare class QuestionsService {
    private questionsRepository;
    private answersRepository;
    constructor(questionsRepository: Repository<Question>, answersRepository: Repository<Answer>);
    findAll(): Promise<Question[]>;
}
