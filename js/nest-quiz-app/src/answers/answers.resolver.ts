import { Query, Resolver } from '@nestjs/graphql';
import { AnswersService } from './answers.service';
import { Answer } from './answer.entity';

@Resolver(of => Answer)
export class AnswersResolver {
    constructor(private answersService: AnswersService) {}

    @Query(returns => [Answer])
    async answers(): Promise<Answer []> {
        return this.answersService.findAll();
    }
}
