import { Answer } from './answer.entity';
import { Repository } from 'typeorm';
export declare class AnswersService {
    private answersRepository;
    constructor(answersRepository: Repository<Answer>);
    findAll(): Promise<Answer[]>;
}
